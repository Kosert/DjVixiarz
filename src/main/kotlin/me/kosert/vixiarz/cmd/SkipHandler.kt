package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object SkipHandler : IHandler {
    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        event.channel.sendMessage(event.voiceController().skip()).complete()
        return true
    }
}
