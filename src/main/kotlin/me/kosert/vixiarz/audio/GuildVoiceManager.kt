package me.kosert.vixiarz.audio

import discord4j.common.util.Snowflake

import java.util.concurrent.ConcurrentHashMap


object GuildVoiceManager {

    private val managers = ConcurrentHashMap<Snowflake, VoiceChannelController>()

    fun getVoice(id: Snowflake): VoiceChannelController {
        return managers.getOrPut(id, { VoiceChannelController() })
    }
}