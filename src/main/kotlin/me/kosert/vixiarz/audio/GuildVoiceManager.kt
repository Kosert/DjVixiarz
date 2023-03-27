package me.kosert.vixiarz.audio

import java.util.concurrent.ConcurrentHashMap


object GuildVoiceManager {

    private val managers = ConcurrentHashMap<String, VoiceChannelController>()

    fun getVoice(id: String): VoiceChannelController {
        return managers.getOrPut(id) { VoiceChannelController() }
    }

    fun getConnectedInfo() = managers.values.mapNotNull { it.getConnectedChannelInfo() }
}