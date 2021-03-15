package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.orNull
import me.kosert.vixiarz.voiceController

object ResumeHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        event.voiceController()?.setPause(false)
        return true
    }
}