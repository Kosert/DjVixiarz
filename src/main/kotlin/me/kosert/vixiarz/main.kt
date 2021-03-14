package me.kosert.vixiarz

import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.VoiceStateUpdateEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.auth.Token
import java.util.*


suspend fun main() {
    println("Starting")
    Token.loadFromFile()
    val client = DiscordClientBuilder.create(Token.get())
            .build()
            .login()
            .block()!!

    client.eventDispatcher.on(ReadyEvent::class.java)
            .subscribe { event: ReadyEvent ->
                val self = event.self
                println("Logged in as ${self.username}#${self.discriminator}")
            }

    client.eventDispatcher.on(MessageCreateEvent::class.java)
            .subscribe { event: MessageCreateEvent ->
                CmdHandler.checkCmd(event)
            }

    client.eventDispatcher.on(VoiceStateUpdateEvent::class.java)
            .subscribe { event ->
                VoiceChannelController.checkIfShouldLeave()
            }


    client.onDisconnect().block()
}

fun <T> Optional<T>.orNull(): T? = orElse(null)