package me.kosert.vixiarz

import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.collect
import me.kosert.vixiarz.Const.START_TAG
import me.kosert.vixiarz.audio.GuildVoiceManager
import me.kosert.vixiarz.auth.Secrets
import me.kosert.vixiarz.cmd.Command
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceUpdateEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import reactor.util.Logger
import reactor.util.Loggers
import java.util.*

val LOG: Logger = Loggers.getLogger("VixaLogger")

suspend fun main() {
    LOG.info("Starting")

    val manager = ReactiveEventManager()

    manager.on<ReadyEvent>()
        .subscribe {
            val user = it.jda.selfUser
            LOG.info("Logged in as ${user.name}#${user.discriminator}")
        }

    CoroutineScope(Job()).launch {
        manager.on<MessageReceivedEvent>()
            .collect { event ->
                val msg = event.message.contentRaw.trim().split(" ").firstOrNull().orEmpty()
                val cmd = Command.values().find {
                    it.aliases.any { alias -> msg.lowercase(Locale.getDefault()) == START_TAG + alias }
                } ?: return@collect

                withContext(Dispatchers.Default) {
                    LOG.info("Handling cmd: ${event.message}")
                    cmd.handlers.firstOrNull {
                        it.handle(event)
                    } ?: run {
                        LOG.warn("Failed to handle command", event)
                    }
                }
            }
    }

    CoroutineScope(Job()).launch {
        manager.on<GenericGuildVoiceUpdateEvent>()
            .collect { event ->
                LOG.info("Voice event: $event")
                GuildVoiceManager.getVoice(event.guild.id).checkIfShouldLeave()
            }
    }

    Secrets.loadFromFile()

    JDABuilder.createDefault(Secrets.getDiscordToken())
        .setEventManager(manager)
        .build()
}