package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object QueueHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        event.channel.sendMessageEmbeds(event.voiceController().queue()).complete()
        return true
    }
}
