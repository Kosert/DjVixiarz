package me.kosert.vixiarz

import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import me.kosert.vixiarz.audio.VoiceChannelController
import me.kosert.vixiarz.audio.GuildVoiceManager
import me.sargunvohra.lib.ktunits.milliseconds
import java.util.*

fun MessageCreateEvent.voiceController(): VoiceChannelController? {
    return guildId.orNull()?.let { GuildVoiceManager.getVoice(it) }
}

fun MessageCreateEvent.channel(): MessageChannel? {
    return message.channel.block()
}

fun <T> Optional<T>.orNull(): T? = orElse(null)

fun Long.formatAsDuration(): String {
    val minutes = milliseconds.toMinutes
    val seconds = milliseconds.toSeconds % 60

    val secondsString = if (seconds < 10) "0$seconds" else seconds
    return "$minutes:$secondsString"
}