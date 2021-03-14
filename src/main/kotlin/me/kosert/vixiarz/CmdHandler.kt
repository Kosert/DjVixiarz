package me.kosert.vixiarz

import kotlin.reflect.KFunction1

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.rest.util.Color
import java.time.Instant


object CmdHandler {

    private const val START_TAG = "!"

    private val handlers: Map<String, KFunction1<MessageCreateEvent, Unit>> = mapOf(
            "ping" to ::ping,
            "help" to ::help,
            "join" to ::join,
            "quit" to ::quit
    )

    fun checkCmd(event: MessageCreateEvent) {
        val msg = event.message.content
        val handler = handlers.entries.find { msg.startsWith(START_TAG + it.key) }

        handler?.value?.invoke(event)
    }

    private fun ping(event: MessageCreateEvent) {
        event.message.channel.block()?.createMessage("Pong")
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
}