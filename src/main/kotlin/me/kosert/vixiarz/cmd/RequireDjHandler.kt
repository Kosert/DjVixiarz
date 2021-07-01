package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.createEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*

object RequireDjHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        val isDJ = event.member
                ?.roles
                ?.any { it.name.uppercase(Locale.getDefault()) == "DJ" }
                ?: false

        if (!isDJ) {
            val embed = createEmbed {
                setTitle("Nie dla psa, dla pana to")
                setDescription("Brak uprawnień")
            }
            event.channel.sendMessage(embed)
            return true
        }

        return false
    }
}