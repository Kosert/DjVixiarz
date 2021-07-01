package me.kosert.vixiarz.cmd

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.nio.file.Paths

abstract class GifHandler : IHandler {

    abstract val gifFilename: String
    open val visibleName: String
        get() = gifFilename

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        val file = Paths.get(System.getProperty("user.dir"), GIF_FOLDER_NAME, gifFilename).toFile()
        event.channel.sendFile(file.inputStream(), visibleName).complete()
        return true
    }

    companion object {
        const val GIF_FOLDER_NAME = "gifs"
    }
}