package me.kosert.vixiarz.audio

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers

import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager


object PlayerManager {

    val PLAYER_MANAGER = DefaultAudioPlayerManager()

    init {
        // This is an optimization strategy that Discord4J can utilize to minimize allocations
        PLAYER_MANAGER.configuration.setFrameBufferFactory { bufferDuration, format, stopping ->
            NonAllocatingAudioFrameBuffer(bufferDuration, format, stopping)
        }

        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER)
        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER)
    }

}