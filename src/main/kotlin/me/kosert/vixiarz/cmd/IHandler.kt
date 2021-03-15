package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent

interface IHandler {

    /**
     * return true if was handled
     */
    suspend fun handle(event: MessageCreateEvent): Boolean
}