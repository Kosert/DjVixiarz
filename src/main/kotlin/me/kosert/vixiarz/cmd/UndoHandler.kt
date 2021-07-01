package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.send
import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object UndoHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        val issuer = event.author.run { "${name}#${discriminator}" }
        event.voiceController().undo(issuer).send(event.channel)
        return true
    }
}
