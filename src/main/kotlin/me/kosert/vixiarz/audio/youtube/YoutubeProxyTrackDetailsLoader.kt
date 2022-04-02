package me.kosert.vixiarz.audio.youtube

import com.sedmelluq.discord.lavaplayer.source.youtube.*
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeHttpContextFilter.PBJ_PARAMETER
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools.throwWithDebugInfo
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.function.Consumer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.COMMON
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS
import me.kosert.vixiarz.LOG
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8


class YoutubeProxyTrackDetailsLoader : YoutubeTrackDetailsLoader {

    private val log: Logger = LoggerFactory.getLogger(DefaultYoutubeTrackDetailsLoader::class.java)

    private val AGE_VERIFY_REQUEST_URL =
        "https://www.youtube.com/youtubei/v1/verify_age?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
    private val AGE_VERIFY_REQUEST_PAYLOAD =
        "{\"context\":{\"client\":{\"clientName\":\"WEB\",\"clientVersion\":\"2.20210302.07.01\"}},\"nextEndpoint\":{\"urlEndpoint\":{\"url\":\"%s\"}},\"setControvercy\":true}"
    private val PLAYER_REQUEST_URL =
        "https://www.youtube.com/youtubei/v1/player?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
    private val PLAYER_REQUEST_PAYLOAD =
        "{\"context\":{\"client\":{\"clientName\":\"ANDROID\",\"clientVersion\":\"16.24\",\"clientScreen\":\"EMBED\"},\"thirdParty\":{\"embedUrl\":\"https://www.youtube.com\"}},\"racyCheckOk\":true,\"contentCheckOk\":true,\"videoId\":\"%s\"}"

    @Volatile
    private var cachedPlayerScript: CachedPlayerScript? = null

    override fun loadDetails(
        httpInterface: HttpInterface,
        videoId: String,
        requireFormats: Boolean
    ): YoutubeTrackDetails? {
        return try {
            load(httpInterface, videoId, requireFormats)
        } catch (e: IOException) {
            throw ExceptionTools.toRuntimeException(e)
        }
    }

    @Throws(IOException::class)
    private fun load(
        httpInterface: HttpInterface,
        videoId: String,
        requireFormats: Boolean
    ): YoutubeTrackDetails? {
        val mainInfo = loadTrackInfoFromMainPage(httpInterface, videoId)
        return try {
            val initialData = loadBaseResponse(mainInfo, httpInterface, videoId, requireFormats)
                ?: return null
            val finalData = augmentWithPlayerScript(initialData, httpInterface, requireFormats)
            DefaultYoutubeTrackDetails(videoId, finalData.toYoutubeJsonData())
        } catch (e: FriendlyException) {
            throw e
        } catch (e: Exception) {
            throw throwWithDebugInfo(
                log,
                e,
                "Error when extracting data",
                "mainJson",
                mainInfo.format()
            )
        }
    }

    @Throws(IOException::class)
    protected fun loadBaseResponse(
        mainInfo: JsonBrowser?,
        httpInterface: HttpInterface,
        videoId: String,
        requireFormats: Boolean
    ): YoutubeProxyTrackJsonData? {
        val data = YoutubeProxyTrackJsonData.fromMainResult(mainInfo!!)
        val status = checkPlayabilityStatus(data.playerResponse)
        LOG.info("Playability status: $status")
        if (status == InfoStatus.DOES_NOT_EXIST) {
            return null
        }
        if (status == InfoStatus.CONTENT_CHECK_REQUIRED) {
            val trackInfo = loadTrackInfoWithContentVerifyRequest(httpInterface, videoId)
            return YoutubeProxyTrackJsonData.fromMainResult(trackInfo)
        }
        return if (requireFormats && status == InfoStatus.REQUIRES_LOGIN) {
            val playerResponse = loadTrackInfoFromProxy(httpInterface, videoId)
            YoutubeProxyTrackJsonData(playerResponse, JsonBrowser.NULL_BROWSER, null)
        } else {
            data
        }
    }

    protected fun checkPlayabilityStatus(playerResponse: JsonBrowser): InfoStatus {
        val statusBlock = playerResponse["playabilityStatus"]
        if (statusBlock.isNull) {
            throw RuntimeException("No playability status block.")
        }
        val status = statusBlock["status"].text()
        return if (status == null) {
            throw RuntimeException("No playability status field.")
        } else if ("OK" == status) {
            InfoStatus.INFO_PRESENT
        } else if ("ERROR" == status) {
            val reason = statusBlock["reason"].text()
            if ("Video unavailable" == reason) {
                InfoStatus.DOES_NOT_EXIST
            } else {
                throw FriendlyException(reason, COMMON, null)
            }
        } else if ("UNPLAYABLE" == status) {
            val unplayableReason = getUnplayableReason(statusBlock)
            throw FriendlyException(unplayableReason, COMMON, null)
        } else if ("LOGIN_REQUIRED" == status) {
            val errorReason = statusBlock["errorScreen"]["playerErrorMessageRenderer"]["reason"]["simpleText"].text()
            if ("Private video" == errorReason) {
                throw FriendlyException("This is a private video.", COMMON, null)
            }
            InfoStatus.REQUIRES_LOGIN
        } else if ("CONTENT_CHECK_REQUIRED" == status) {
            InfoStatus.CONTENT_CHECK_REQUIRED
        } else {
            throw FriendlyException("This video cannot be viewed anonymously.", COMMON, null)
        }
    }

    protected enum class InfoStatus {
        INFO_PRESENT, REQUIRES_LOGIN, DOES_NOT_EXIST, CONTENT_CHECK_REQUIRED
    }

    protected fun getUnplayableReason(statusBlock: JsonBrowser): String {
        val playerErrorMessage = statusBlock["errorScreen"]["playerErrorMessageRenderer"]
        var unplayableReason = statusBlock["reason"].text()
        if (!playerErrorMessage["subreason"].isNull) {
            val subreason = playerErrorMessage["subreason"]
            if (!subreason["simpleText"].isNull) {
                unplayableReason = subreason["simpleText"].text()
            } else if (!subreason["runs"].isNull && subreason["runs"].isList) {
                val reasonBuilder = StringBuilder()
                subreason["runs"].values().forEach(
                    Consumer { item: JsonBrowser ->
                        reasonBuilder.append(
                            item["text"].text()
                        ).append('\n')
                    }
                )
                unplayableReason = reasonBuilder.toString()
            }
        }
        return unplayableReason
    }

    @Throws(IOException::class)
    protected fun loadTrackInfoFromMainPage(
        httpInterface: HttpInterface,
        videoId: String
    ): JsonBrowser {
        val url = "https://www.youtube.com/watch?v=" + videoId + PBJ_PARAMETER + "&hl=en"
        httpInterface.execute(HttpGet(url)).use { response ->
            HttpClientTools.assertSuccessWithContent(response, "video page response")
            val responseText: String = EntityUtils.toString(response.entity, UTF_8)
            return try {
                JsonBrowser.parse(responseText)
            } catch (e: FriendlyException) {
                throw e
            } catch (e: Exception) {
                throw FriendlyException(
                    "Received unexpected response from YouTube.", SUSPICIOUS,
                    RuntimeException("Failed to parse: $responseText", e)
                )
            }
        }
    }
    
    @Throws(IOException::class)
    protected fun loadTrackInfoFromProxy(
        httpInterface: HttpInterface,
        videoId: String?
    ): JsonBrowser {
        val config = InnerTubeConfiguration.default()
        val url = "https://youtube-proxy.zerody.one/getPlayer" +
                "?videoId=${URLEncoder.encode(videoId, "utf-8")}" +
                "&reason=${URLEncoder.encode("LOGIN_REQUIRED", "utf-8")}" +
                "&clientName=${config.INNERTUBE_CLIENT_NAME}" +
                "&clientVersion=${config.INNERTUBE_CLIENT_VERSION}" +
                "&signatureTimestamp=${config.STS}"

        val get = HttpGet(url)
        httpInterface.execute(get).use { response ->
            HttpClientTools.assertSuccessWithContent(response, "video info response")
            val json: JsonBrowser = JsonBrowser.parse(EntityUtils.toString(response.entity, UTF_8))
            return json
        }
    }

    @Throws(IOException::class)
    protected fun loadTrackInfoWithContentVerifyRequest(
        httpInterface: HttpInterface,
        videoId: String
    ): JsonBrowser {
        val post = HttpPost(AGE_VERIFY_REQUEST_URL)
        val payload = StringEntity(
            String.format(
                AGE_VERIFY_REQUEST_PAYLOAD,
                "/watch?v=$videoId"
            ), "UTF-8"
        )
        post.entity = payload
        httpInterface.execute(post).use { response ->
            HttpClientTools.assertSuccessWithContent(response, "content verify response")
            val json: String = EntityUtils.toString(response.entity, UTF_8)
            val fetchedContentVerifiedLink = JsonBrowser.parse(json)["actions"]
                .index(0)["navigateAction"]["endpoint"]["urlEndpoint"]["url"]
                .text()
            if (fetchedContentVerifiedLink != null) {
                return loadTrackInfoFromMainPage(
                    httpInterface,
                    fetchedContentVerifiedLink.substring(9)
                )
            }
            log.error(
                "Did not receive requested content verified link on track {} response: {}",
                videoId,
                json
            )
        }
        throw FriendlyException(
            "Track requires content verification.", SUSPICIOUS,
            IllegalStateException("Expected response is not present.")
        )
    }

    @Throws(IOException::class)
    protected fun augmentWithPlayerScript(
        data: YoutubeProxyTrackJsonData,
        httpInterface: HttpInterface,
        requireFormats: Boolean
    ): YoutubeProxyTrackJsonData {
        val now = System.currentTimeMillis()
        if (data.playerScriptUrl != null) {
            cachedPlayerScript = CachedPlayerScript(data.playerScriptUrl, now)
            return data
        } else if (!requireFormats) {
            return data
        }
        val cached = cachedPlayerScript
        if (cached != null && cached.timestamp + 600000L >= now) {
            return data.withPlayerScriptUrl(cached.playerScriptUrl)
        }
        httpInterface.execute(HttpGet("https://www.youtube.com")).use { response ->
            HttpClientTools.assertSuccessWithContent(response, "youtube root")
            val responseText = EntityUtils.toString(response.entity)
            val encodedUrl =
                DataFormatTools.extractBetween(responseText, "\"PLAYER_JS_URL\":\"", "\"")
                    ?: throw throwWithDebugInfo(
                        log,
                        null,
                        "no PLAYER_JS_URL in youtube root",
                        "html",
                        responseText
                    )
            val fetchedPlayerScript =
                JsonBrowser.parse("{\"url\":\"$encodedUrl\"}")["url"].text()
            cachedPlayerScript = CachedPlayerScript(fetchedPlayerScript, now)
            return data.withPlayerScriptUrl(fetchedPlayerScript)
        }
    }

    protected class CachedPlayerScript(val playerScriptUrl: String, val timestamp: Long)


}

private fun YoutubeProxyTrackJsonData.toYoutubeJsonData(): YoutubeTrackJsonData {
    return YoutubeTrackJsonData(playerResponse, polymerArguments, playerScriptUrl)
}
