package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.rest.util.Color
import me.kosert.vixiarz.orNull
import me.kosert.vixiarz.voiceController

object JoinHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        val voiceState = event.member.orNull()?.voiceState?.block()
        val channel = voiceState?.channel?.block() ?: return false
        event.voiceController()?.join(channel)
        return true
    }
}