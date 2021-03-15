package me.kosert.vixiarz.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlin.jvm.JvmOverloads
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.Collections
import java.util.LinkedList

class AudioTrackScheduler(
    private val player: AudioPlayer
) : AudioEventAdapter() {

    // The queue may be modifed by different threads so guarantee memory safety
    // This does not, however, remove several race conditions currently present
    private val queue: MutableList<AudioTrack> = Collections.synchronizedList(LinkedList())

    fun getQueue(): List<AudioTrack> = queue

    @JvmOverloads
    fun play(track: AudioTrack, force: Boolean = false): Boolean {
        val playing = player.startTrack(track, !force)
        if (!playing) {
            queue.add(track)
        }
        return playing
    }

    fun setPause(pause: Boolean) {
        player.isPaused = pause
    }

    fun skip(): Boolean {
        if (player.playingTrack == null)
            return false

        if (queue.isNotEmpty())
            play(queue.removeAt(0), true)
        else
            player.stopTrack()

        return true
    }

    override fun onTrackEnd(
        player: AudioPlayer,
        track: AudioTrack,
        endReason: AudioTrackEndReason
    ) {
        // Advance the player if the track completed naturally (FINISHED) or if the track cannot play (LOAD_FAILED)
        if (endReason.mayStartNext && queue.isNotEmpty())
            play(queue.removeAt(0), true)
    }

    fun clear() {
        player.stopTrack()
        queue.clear()
    }
}