package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.voiceController

object QuitHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        event.voiceController()?.leave()
        return true
    }
}