package me.kosert.vixiarz

import kotlin.reflect.KFunction1

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.rest.util.Color
import java.time.Instant


object CmdHandler {

    private const val START_TAG = "!"

    private val handlers: Map<List<String>, KFunction1<MessageCreateEvent, Unit>> = mapOf(
            listOf("ping") to ::ping,
            listOf("help", "?") to ::help,
            listOf("join") to ::join,
            listOf("quit", "leave") to ::quit,
            listOf("play", "p") to ::play
    )

    fun checkCmd(event: MessageCreateEvent) {
        val msg = event.message.content
        val handler = handlers.entries.find {
            it.key.any { alias -> msg.startsWith(START_TAG + alias) }
        }

        handler?.value?.invoke(event)
    }

    private fun ping(event: MessageCreateEvent) {
        event.message.channel.block()?.createMessage("Pong")?.block()
    }

    private fun help(event: MessageCreateEvent) {
        event.message.channel.block()?.createEmbed {
            it.apply {
                setAuthor("DJ VIXIARZ", null, event.client.self.block()?.avatarUrl)
                setColor(Color.PINK)
                setDescription("Help:")
                addField("!ping", "Pong", true)
                addField("!join", "Join voice channel", true)
                addField("!quit", "Leave voice channel", true)
                setFooter("Jebać pachure ( ͡° ͜ʖ ͡°)", null)
            }
        }?.block()
    }

    private fun join(event: MessageCreateEvent) {
        val voiceState = event.member.orNull()?.voiceState?.block()
        val channel = voiceState?.channel?.block() ?: return

        VoiceChannelController.join(channel)
    }

    private fun quit(event: MessageCreateEvent) {
        VoiceChannelController.leave()
    }

    private fun play(event: MessageCreateEvent) {
        TODO()
    }
}