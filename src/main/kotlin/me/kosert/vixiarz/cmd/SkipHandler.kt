package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.LOG
import me.kosert.vixiarz.channel
import me.kosert.vixiarz.voiceController

object SkipHandler : IHandler {
    override suspend fun handle(event: MessageCreateEvent): Boolean {
        LOG.info("Skip")
        event.channel()?.createEmbed {
            event.voiceController()?.skip()?.create(it)
        }?.block()
        return true
    }
}
