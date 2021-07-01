package me.kosert.vixiarz.cmd

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface IHandler {

    /**
     * return true if was handled
     */
    suspend fun handle(event: MessageReceivedEvent): Boolean
}