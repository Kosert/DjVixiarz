package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.rest.util.Color
import me.kosert.vixiarz.Const.FOOTER_TEXT
import me.kosert.vixiarz.channel
import me.kosert.vixiarz.orNull

object RequireDjHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        val isDJ = event.member
                .orNull()
                ?.roles
                ?.any { it.name.toUpperCase() == "DJ" }
                ?.block()
                ?: false

        if (!isDJ) {
            event.channel()?.createEmbed {
                it.setTitle("Nie dla psa, dla pana to")
                it.setDescription("Brak uprawnień")
                it.setColor(Color.PINK)
                it.setFooter(FOOTER_TEXT, null)
            }?.block()
            return true
        }

        return false
    }
}