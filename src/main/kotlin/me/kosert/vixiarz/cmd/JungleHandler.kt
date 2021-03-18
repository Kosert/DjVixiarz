package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.channel
import java.nio.file.Paths

object JungleHandler : IHandler {

    private const val GIF_FILENAME = "gorillagamgam.gif"

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        event.channel()?.createMessage {
            val file = Paths.get(System.getProperty("user.dir"), GIF_FILENAME).toFile()
            it.addFile(GIF_FILENAME, file.inputStream())
        }?.block()
        return true
    }

}
