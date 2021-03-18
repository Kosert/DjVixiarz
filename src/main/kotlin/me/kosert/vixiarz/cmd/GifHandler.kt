package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.channel
import java.nio.file.Paths

abstract class GifHandler : IHandler {

    abstract val gifFilename: String
    open val visibleName: String
        get() = gifFilename

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        event.channel()?.createMessage {
            val file = Paths.get(System.getProperty("user.dir"), GIF_FOLDER_NAME, gifFilename).toFile()
            it.addFile(visibleName, file.inputStream())
        }?.block()
        return true
    }

    companion object {
        const val GIF_FOLDER_NAME = "gifs"
    }
}