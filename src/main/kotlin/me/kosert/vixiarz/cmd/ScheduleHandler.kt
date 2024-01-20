package me.kosert.vixiarz.cmd

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.result.getOrElse
import me.kosert.vixiarz.LOG
import me.kosert.vixiarz.auth.Secrets
import me.kosert.vixiarz.createEmbed
import me.kosert.vixiarz.isToday
import me.kosert.vixiarz.models.Presence
import me.kosert.vixiarz.send
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

@Deprecated("RIP Kotożercy")
object ScheduleHandler : IHandler {
    private const val FILE_URL = "https://www.googleapis.com/drive/v3/files/1YWNRFLHVzL60A9HolsYutR3Cz_63wMv82HzEm2-LDbE/export"

    override suspend fun handle(event: MessageReceivedEvent): Boolean {

        val result = Fuel.get(FILE_URL).apply {
            parameters = listOf(
                "mimeType" to "text/csv",
                "key" to Secrets.getGoogleApiKey()
            )
        }.awaitStringResult()

        val csv = result.getOrElse {
            LOG.error("Error fetching google doc: $it")
            return false
        }

        val rows = csv.lineSequence()
            .drop(1)
            .mapNotNull { Presence.fromCsvRow(it) }
            .filter { it.timestamp.isToday() }
            .sortedBy { !it.isPlaying }
            .map { it.generateEmbedRow() }
            .joinToString("|\n")

        val desc =
            "```Godzina: 16   17   18   19   20   21   22   23   00\n$rows```"

        createEmbed {
            setTitle("Kto dzisiaj gra?")
            setDescription(desc)
        }.send(event.channel)

        return true
    }


}