package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.createEmbed
import me.kosert.vixiarz.send
import me.kosert.vixiarz.sendError
import me.kosert.vixiarz.voiceController
import me.sargunvohra.lib.ktunits.minutes
import me.sargunvohra.lib.ktunits.seconds
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class SeekHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        val tokens = event.message.contentRaw
            .split(" ")
            .filter { it.isNotBlank() }

        if (tokens.size < 2) {
            event.channel.sendError("Nie dałeś czasu gościu")
            return true
        }

        if (event.voiceController().currentSong?.isSeekable != true) {
            event.channel.sendError("Nic teraz nie gra lub nie da się przewinąć")
            return true
        }

        val time = tokens[1].toIntOrNull()?.seconds
            ?: tokens[1].split(":").takeIf { it.size == 2 }?.let {
                val min = it.getOrNull(0)?.toIntOrNull()?.minutes ?: 0.minutes
                val sec = it.getOrNull(1)?.toIntOrNull()?.seconds ?: 0.seconds
                min + sec
            }
            ?: run {
                event.channel.sendError("Niepoprawny format czasu")
                return true
            }

        event.voiceController().currentSong?.position = time.toMilliseconds
        createEmbed {
            setDescription("Przewijam...")
        }.send(event.channel)
        return true
    }
}