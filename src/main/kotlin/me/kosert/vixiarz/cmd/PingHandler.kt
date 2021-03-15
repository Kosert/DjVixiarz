package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.orNull

object PingHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        event.message.channel.block()?.createMessage("Pong")?.block()
        return true
    }
}