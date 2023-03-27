package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.audio.GuildVoiceManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object JoinHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        val channel = event.member?.voiceState?.channel ?: return false
        val guildId = channel.guild.id
        GuildVoiceManager.getVoice(guildId).join(channel.asVoiceChannel())
        return true
    }
}