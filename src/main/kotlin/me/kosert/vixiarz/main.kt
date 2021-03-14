package me.kosert.vixiarz

import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import me.kosert.vixiarz.auth.Token
import discord4j.core.`object`.entity.User

import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel

import discord4j.core.event.domain.message.MessageCreateEvent
import org.reactivestreams.Publisher
import java.util.function.Function


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
            .map { obj: MessageCreateEvent -> obj.message }
            .filter {
                message: Message -> message.author.map { user: User -> !user.isBot }.orElse(false)
            }
            .filter {
                message: Message -> message.content.equals("!ping", ignoreCase = true)
            }
            .flatMap<MessageChannel>(Function<Message, Publisher<out MessageChannel>> {
                obj: Message -> obj.channel
            })
            .flatMap(Function<MessageChannel, Publisher<out Message>> {
                channel: MessageChannel -> channel.createMessage("Pong!")
            })
            .subscribe()

    client.onDisconnect().block()


}