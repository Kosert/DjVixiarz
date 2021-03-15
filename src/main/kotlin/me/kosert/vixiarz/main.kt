package me.kosert.vixiarz

import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.VoiceStateUpdateEvent
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactor.mono
import me.kosert.vixiarz.Const.START_TAG
import me.kosert.vixiarz.auth.Token
import me.kosert.vixiarz.cmd.Command
import me.kosert.vixiarz.audio.GuildVoiceManager
import reactor.util.Logger
import reactor.util.Loggers
import java.util.*

val LOG: Logger = Loggers.getLogger("VixaLogger")

suspend fun main() {
    LOG.info("Starting")
    Token.loadFromFile()
    val client = DiscordClientBuilder.create(Token.get())
            .build()
            .login()
            .block()!!

    client.eventDispatcher.on(ReadyEvent::class.java)
            .subscribe { event: ReadyEvent ->
                val self = event.self
                LOG.info("Logged in as ${self.username}#${self.discriminator}")
            }

    client.eventDispatcher.on(MessageCreateEvent::class.java)
            .subscribe { event: MessageCreateEvent ->
                val msg = event.message.content.trim().split(" ").firstOrNull().orEmpty()
                val cmd = Command.values().find {
                    it.aliases.any { alias -> msg == START_TAG + alias }
                } ?: return@subscribe

                mono {
                    cmd.handlers.firstOrNull {
                        it.handle(event)
                    } ?: run {
                        LOG.warn("Failed to handle command", event)
                    }
                }.block()
            }

    client.eventDispatcher.on(VoiceStateUpdateEvent::class.java)
            .subscribe { event ->
                GuildVoiceManager.getVoice(event.current.guildId).checkIfShouldLeave()
            }


    client.onDisconnect().block()
}