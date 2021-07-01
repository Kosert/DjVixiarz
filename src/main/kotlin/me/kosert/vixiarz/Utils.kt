package me.kosert.vixiarz

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.kosert.vixiarz.audio.GuildVoiceManager
import me.kosert.vixiarz.audio.VoiceChannelController
import me.sargunvohra.lib.ktunits.milliseconds
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import java.util.*

fun MessageReceivedEvent.voiceController(): VoiceChannelController {
    return this.guild.id.let { GuildVoiceManager.getVoice(it) }
}

suspend fun MessageChannel.sendError(text: String) = withContext(Dispatchers.IO) {
    val embed = createEmbed {
        setTitle(Const.ERROR_TITLE)
        setDescription(text)
        setColor(Color.PINK)
        setFooter(Const.FOOTER_TEXT, null)
    }
    sendMessage(embed).complete()
}

fun <T> Optional<T>.orNull(): T? = orElse(null)

fun Long.formatAsDuration(): String {
    val minutes = milliseconds.toMinutes
    val seconds = milliseconds.toSeconds % 60

    val secondsString = if (seconds < 10) "0$seconds" else seconds
    return "$minutes:$secondsString"
}

fun MessageEmbed.send(channel: MessageChannel) = channel.sendMessage(this).complete()
