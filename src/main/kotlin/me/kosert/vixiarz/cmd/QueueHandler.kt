package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.channel
import me.kosert.vixiarz.voiceController

object QueueHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {

        event.channel()?.createEmbed {
            event.voiceController()?.queue()?.create(it)
        }?.block()
        return true
    }
}
