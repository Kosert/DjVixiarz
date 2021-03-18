package me.kosert.vixiarz.searcher

import com.google.gson.Gson
import kotlinx.coroutines.*
import me.kosert.vixiarz.LOG
import me.sargunvohra.lib.ktunits.seconds
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Paths

object MusicSearcher {

    private const val CMD = "node"
    private const val SUBPROJECT_FOLDER = "youtubeSearcher"
    private const val SUBPROJECT_ENTRY = "index.js"

    suspend fun search(query: String): SearchedVideo? {
        val json = fetchJson(query)
        if (json.isNullOrBlank())
            return null

        return runCatching {
            val gson = Gson()
            return gson.fromJson(json, SearchResponseModel::class.java)?.model
        }.getOrNull()
    }

    private suspend fun fetchJson(query: String): String? {
        val dirPath = Paths.get(System.getProperty("user.dir"), SUBPROJECT_FOLDER)
        val dir = dirPath.toFile()

        val cmd = "$CMD $SUBPROJECT_ENTRY $query"

        return withContext(Dispatchers.IO) {
            withTimeoutOrNull(10.seconds.toMilliseconds) {
                exec(cmd, dir)
            }
        }
    }

    /** Run a system-level command.
     * Note: This is a system independent java exec (e.g. | doesn't work). For shell: prefix with "bash -c"
     * Inputting the string in stdIn (if any), and returning stdout and stderr as a string. */
    private suspend fun exec(
            cmd: String,
            workingDir: File = File(".")
    ): String? {
        return try {
            LOG.info("before start")
            val process = ProcessBuilder(*cmd.split("\\s".toRegex()).toTypedArray())
                    .directory(workingDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            LOG.info("before while")
            process.inputStream.awaitData().decodeToString()
        } catch (e: IOException) {
            LOG.error("Chujoza", e)
            null
        }
    }

    private suspend fun InputStream.awaitData() = withContext(Dispatchers.IO) {
        while (available() == 0) {
            delay(10)
        }
        val count = available()
        val buffer = ByteArray(count)
        read(buffer, 0, count)
        return@withContext buffer
    }

}