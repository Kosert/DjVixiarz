package me.kosert.vixiarz.audio

import discord4j.voice.AudioProvider
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import java.nio.ByteBuffer

class LavaPlayerAudioProvider(
        private val player: AudioPlayer
) : AudioProvider(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())) {

    private val frame = MutableAudioFrame()

    init {
        // Allocate a ByteBuffer for Discord4J's AudioProvider to hold audio data for Discord
        // Set LavaPlayer's AudioFrame to use the same buffer as Discord4J's
        frame.setBuffer(buffer)
    }

    override fun provide(): Boolean {
        // AudioPlayer writes audio data to the AudioFrame
        val didProvide = player.provide(frame)
        if (didProvide) {
            buffer.flip()
        }
        return didProvide
    }
}