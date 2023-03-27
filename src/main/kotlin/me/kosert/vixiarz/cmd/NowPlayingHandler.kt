package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object NowPlayingHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        event.channel.sendMessageEmbeds(event.voiceController().nowPlaying()).complete()
        return true
    }

}
