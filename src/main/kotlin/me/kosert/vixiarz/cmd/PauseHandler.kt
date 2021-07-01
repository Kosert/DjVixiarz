package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object PauseHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        event.voiceController().setPause(true)?.let {
            event.channel.sendMessage(it).complete()
        }
        return true
    }
}