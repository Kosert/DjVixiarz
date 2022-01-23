package me.kosert.vixiarz

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import me.kosert.vixiarz.audio.GuildVoiceManager
import me.kosert.vixiarz.audio.VoiceChannelController
import me.sargunvohra.lib.ktunits.milliseconds
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import java.util.Optional

fun MessageReceivedEvent.voiceController(): VoiceChannelController {
    return this.guild.id.let { GuildVoiceManager.getVoice(it) }
}

suspend fun MessageChannel.sendError(text: String) = withContext(Dispatchers.IO) {
    val embed = createEmbed {
        setTitle(Const.ERROR_TITLE)
        setDescription(text)
        setColor(Color.PINK)
        setFooter(FooterGenerator.generate(), null)
    }
    sendMessage(embed).complete()
}

fun <T> Optional<T>.orNull(): T? = orElse(null)

fun Long.formatAsDuration(asText: Boolean = false): String = if (!asText) {
    val minutes = milliseconds.toMinutes
    val seconds = milliseconds.toSeconds % 60
    val secondsString = if (seconds < 10) "0$seconds" else seconds
    "$minutes:$secondsString"
}
else {
    val hours = milliseconds.toHours.takeIf { it > 0 }?.let { it.toString() + "h" }
    val minutes = (milliseconds.toMinutes % 60).takeIf { it > 0 }?.let { it.toString() + "min" }
    val seconds = (milliseconds.toSeconds % 60).takeIf { it > 0 }?.let { it.toString() + "s" }
    listOfNotNull(hours, minutes, seconds).joinToString(" ")
}

fun MessageEmbed.send(channel: MessageChannel) = channel.sendMessage(this).complete()

fun Throwable.causesSequence(): Sequence<Throwable> {
    return generateSequence(this.cause) {
        it.cause.takeUnless { it === this }
    }
}

fun <T> Collection<T>.prepend(element: T): List<T> = listOf(element) + this

fun LocalDateTime.isToday(): Boolean {
    val today = Clock.System.todayAt(TimeZone.currentSystemDefault())
    return today == this.date
}

fun Long.addIf(amount: Long, predicate: () -> Boolean): Long {
    return if (predicate()) this + amount
    else this
}