package me.kosert.vixiarz.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import me.kosert.vixiarz.LOG
import java.util.*

class AudioTrackScheduler(
    private val player: AudioPlayer,
    private val exceptionHandler: (Exception, AudioTrack?) -> Unit
) : AudioEventAdapter() {

    // The queue may be modified by different threads so guarantee memory safety
    // This does not, however, remove several race conditions currently present
    private val queue: MutableList<AudioTrack> = Collections.synchronizedList(LinkedList())

    fun getQueue(): List<AudioTrack> = queue

    private var lastFail: Pair<Long, AudioTrack>? = null

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

    /**
     * index 0 = first item in queue (does not include playing track)
     */
    fun remove(index: Int): AudioTrack? {
        return if (index in 0..queue.lastIndex) {
            queue.removeAt(index)
        } else null
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
        LOG.info("TrackEnd: $endReason, mayStartNext: ${endReason.mayStartNext} - $track")

        val canRetry = if (endReason == AudioTrackEndReason.LOAD_FAILED) {
            // don't retry if fail was same song and just now
            lastFail?.let {
                val sameSongFail = it.second.identifier == track.identifier
                val failWasMomentAgo = System.currentTimeMillis() - it.first < 2000
                val timeDiff = System.currentTimeMillis() - it.first
                LOG.info("sameSongFail: $sameSongFail, failWasMomentAgo: ${failWasMomentAgo} | $timeDiff")
                !sameSongFail || !failWasMomentAgo
            } ?: true
        } else false

        lastFail = System.currentTimeMillis() to track

        when {
            canRetry -> {
                val cloned = track.makeClone()
                LOG.info("Retrying: $cloned")
                player.startTrack(cloned, true)
            }
            endReason.mayStartNext && queue.isNotEmpty() -> {
                play(queue.removeAt(0), true)
            }
        }
    }

    override fun onTrackException(
        player: AudioPlayer,
        track: AudioTrack?,
        exception: FriendlyException
    ) {
        LOG.info("onTrackException: $track")
        exceptionHandler(exception, track)
    }

    fun clear() {
        player.stopTrack()
        queue.clear()
    }
}