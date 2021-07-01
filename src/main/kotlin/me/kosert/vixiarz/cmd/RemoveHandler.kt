package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.send
import me.kosert.vixiarz.sendError
import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object RemoveHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        val tokens = event.message.contentRaw.split(" ").filter { it.isNotBlank() }

        if (tokens.size < 2) {
            event.channel.sendError("Nie podałeś numerka gościu")
            return true
        }

        tokens[1].toIntOrNull()?.takeIf { it >= 0 }?.let {
            event.voiceController().remove(it).send(event.channel)
        } ?: run {
            event.channel.sendError("To niezły numerek gościu xD")
        }
        return true
    }
}
