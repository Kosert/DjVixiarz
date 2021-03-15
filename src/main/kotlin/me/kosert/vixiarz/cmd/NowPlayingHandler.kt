package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.channel
import me.kosert.vixiarz.voiceController

object NowPlayingHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        event.channel()?.createEmbed {
            event.voiceController()?.nowPlaying()?.create(it)
        }?.block()
        return true
    }

}
