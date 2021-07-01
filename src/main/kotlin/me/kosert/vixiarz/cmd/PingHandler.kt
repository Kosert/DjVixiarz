package me.kosert.vixiarz.cmd

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object PingHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        event.message.channel.sendMessage("Pong").complete()
        return true
    }
}