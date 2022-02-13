package me.kosert.vixiarz.audio

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.*
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import me.kosert.vixiarz.audio.youtube.YoutubeProxyTrackDetailsLoader


object PlayerManager {

    val PLAYER_MANAGER = DefaultAudioPlayerManager()

    init {
        // This is an optimization strategy that Discord4J can utilize to minimize allocations
        PLAYER_MANAGER.configuration.setFrameBufferFactory { bufferDuration, format, stopping ->
            NonAllocatingAudioFrameBuffer(bufferDuration, format, stopping)
        }

        AudioSourceManagers.registerLocalSource(PLAYER_MANAGER)

        // Allow playerManager to parse remote sources like YouTube links
        // AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER)

        PLAYER_MANAGER.registerSourceManager(
            YoutubeAudioSourceManager(
                false,
                YoutubeProxyTrackDetailsLoader(),
                YoutubeSearchProvider(),
                YoutubeSearchMusicProvider(),
                YoutubeSignatureCipherManager(),
                DefaultYoutubePlaylistLoader(),
                DefaultYoutubeLinkRouter(),
                YoutubeMixProvider()
            )
        )
        PLAYER_MANAGER.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
        PLAYER_MANAGER.registerSourceManager(BandcampAudioSourceManager())
        PLAYER_MANAGER.registerSourceManager(VimeoAudioSourceManager())
        PLAYER_MANAGER.registerSourceManager(TwitchStreamAudioSourceManager())
        PLAYER_MANAGER.registerSourceManager(BeamAudioSourceManager())
        PLAYER_MANAGER.registerSourceManager(GetyarnAudioSourceManager())
        PLAYER_MANAGER.registerSourceManager(HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY))
    }

}