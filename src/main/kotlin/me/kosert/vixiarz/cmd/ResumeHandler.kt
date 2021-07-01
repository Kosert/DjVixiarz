package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object ResumeHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        event.voiceController().setPause(false)?.let {
            event.channel.sendMessage(it).complete()
        }
        return true
    }
}