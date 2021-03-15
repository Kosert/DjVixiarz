package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.orNull
import me.kosert.vixiarz.voiceController

object PauseHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        event.voiceController()?.setPause(true)
        return true
    }
}