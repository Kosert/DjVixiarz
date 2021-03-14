package me.kosert.vixiarz

import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.spec.VoiceChannelJoinSpec

object VoiceChannelController {

    private var currentChannel: VoiceChannel? = null

    fun join(channel: VoiceChannel) {
        channel.join { spec: VoiceChannelJoinSpec ->
            //spec.setProvider(LavaPlayerAudioProvider())
        }?.block()

        currentChannel = channel
    }

    fun checkIfShouldLeave() {
        val connected = currentChannel?.voiceStates?.count()?.block() ?: return
        if (connected == 1L) {
            println("Bot is alone, leaving")
            leave()
        }
    }

    fun leave() {
        currentChannel?.voiceConnection?.block()?.disconnect()?.block()
        currentChannel = null
    }


}