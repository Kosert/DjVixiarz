package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.Const.ERROR_TITLE
import me.kosert.vixiarz.channel
import me.kosert.vixiarz.orNull
import me.kosert.vixiarz.voiceController
import java.util.function.Consumer

object PlayHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        val tokens = event.message.content.split(" ").filter { it.isNotBlank() }

        if (tokens.size < 2) {
            event.channel()?.createEmbed {
                it.setTitle(ERROR_TITLE)
                it.setDescription("Nie dałeś URL gościu")
            }?.block()
            return true
        }

        if (event.voiceController()?.isJoined() != true) {
            val joined = JoinHandler.handle(event)
            if (!joined)
                return false
        }

        val url = tokens[1]
        val adder = event.member.get()
        val embedCreator = event.voiceController()?.play(adder, url)
        event.channel()?.createEmbed { embedCreator?.create(it) }?.block()
        return true
    }
}
