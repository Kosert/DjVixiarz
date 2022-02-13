package me.kosert.vixiarz.audio.youtube

import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

class YoutubeProxyTrackJsonData(
    val playerResponse: JsonBrowser,
    val polymerArguments: JsonBrowser,
    val playerScriptUrl: String?
) {
    fun withPlayerScriptUrl(playerScriptUrl: String?): YoutubeProxyTrackJsonData {
        return YoutubeProxyTrackJsonData(playerResponse, polymerArguments, playerScriptUrl)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(
            YoutubeProxyTrackDetailsLoader::class.java
        )

        fun fromMainResult(result: JsonBrowser): YoutubeProxyTrackJsonData {
            try {
                var playerInfo = JsonBrowser.NULL_BROWSER
                var playerResponse = JsonBrowser.NULL_BROWSER
                for (child in result.values()) {
                    if (child.isMap) {
                        if (playerInfo.isNull) {
                            playerInfo = child["player"]
                        }
                        if (playerResponse.isNull) {
                            playerResponse = child["playerResponse"]
                        }
                    } else {
                        if (playerResponse.isNull) {
                            playerResponse = result
                        }
                    }
                }
                if (!playerInfo.isNull) {
                    return fromPolymerPlayerInfo(playerInfo, playerResponse)
                } else if (!playerResponse.isNull) {
                    return YoutubeProxyTrackJsonData(playerResponse, JsonBrowser.NULL_BROWSER, null)
                }
            } catch (e: Exception) {
                throw ExceptionTools.throwWithDebugInfo(
                    log,
                    e,
                    "Error parsing result",
                    "json",
                    result.format()
                )
            }
            throw ExceptionTools.throwWithDebugInfo(
                log,
                null,
                "Neither player nor playerResponse in result",
                "json",
                result.format()
            )
        }

        private fun fromPolymerPlayerInfo(
            playerInfo: JsonBrowser,
            playerResponse: JsonBrowser
        ): YoutubeProxyTrackJsonData {
            val args = playerInfo["args"]
            val playerScriptUrl = playerInfo["assets"]["js"].text()
            val playerResponseText = args["player_response"].text()
                ?: // In case of Polymer, the playerResponse with formats is the one embedded in args, NOT the one in outer JSON.
                // However, if no player_response is available, use the outer playerResponse.
                return YoutubeProxyTrackJsonData(
                    playerResponse,
                    args,
                    playerScriptUrl
                )
            return YoutubeProxyTrackJsonData(
                parsePlayerResponse(playerResponseText),
                args,
                playerScriptUrl
            )
        }

        private fun parsePlayerResponse(playerResponseText: String): JsonBrowser {
            return try {
                JsonBrowser.parse(playerResponseText)
            } catch (e: Exception) {
                throw ExceptionTools.throwWithDebugInfo(
                    log,
                    e,
                    "Failed to parse player_response",
                    "value",
                    playerResponseText
                )
            }
        }
    }
}