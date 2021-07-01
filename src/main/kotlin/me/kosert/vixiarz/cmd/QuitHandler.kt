package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object QuitHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        event.voiceController().leave()
        return true
    }
}