package me.kosert.vixiarz.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.spec.VoiceChannelJoinSpec
import me.kosert.vixiarz.Const.ERROR_TITLE
import me.kosert.vixiarz.EmbedCreator
import me.kosert.vixiarz.LOG
import me.kosert.vixiarz.audio.PlayerManager.PLAYER_MANAGER
import me.kosert.vixiarz.formatAsDuration
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class VoiceChannelController {

    private var currentChannel: VoiceChannel? = null

    private val player = PLAYER_MANAGER.createPlayer()
    private val scheduler = AudioTrackScheduler(player)
    private val provider = LavaPlayerAudioProvider(player)

    init {
        player.addListener(scheduler)
    }

    fun isJoined() = currentChannel?.voiceConnection?.block()?.isConnected?.block() ?: false

    fun join(channel: VoiceChannel) {
        channel.join { spec: VoiceChannelJoinSpec ->
            spec.setProvider(provider)
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
        scheduler.clear()
        currentChannel?.voiceConnection?.block()?.disconnect()?.block()
        currentChannel = null
    }

    suspend fun play(user: Member, url: String): EmbedCreator {

        return suspendCoroutine { cont ->
            PLAYER_MANAGER.loadItemOrdered(this, url, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    track.userData = SongInfo("${user.username}#${user.discriminator}", user.avatarUrl)
                    scheduler.play(track)

                    cont.resume(EmbedCreator {
                        it.setTitle("Dodaje:")
                        it.setDescription(track.info.title)
                        it.addField("Autor", track.info.author ?: "Nieznany", true)
                        it.addField("Długość", track.info.length.formatAsDuration(), true)
                    })
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    //todo handle playlists
                    cont.resume(EmbedCreator {
                        it.setTitle(ERROR_TITLE)
                        it.setDescription("Nie obsługuje jeszcze playlist byczq")
                    })
                }

                override fun noMatches() {
                    cont.resume(EmbedCreator {
                        it.setTitle(ERROR_TITLE)
                        it.setDescription("Nie znaleziono :/")
                    })
                }

                override fun loadFailed(exception: FriendlyException?) {
                    LOG.error("Dojebało exception", exception)
                    cont.resume(EmbedCreator {
                        it.setDescription("Dojebało exception: $exception")
                    })
                }
            })
        }
    }

    fun setPause(pause: Boolean): EmbedCreator? {
        if (player.isPaused == pause) {
            return null
        }

        scheduler.setPause(pause)
        return if (pause) EmbedCreator {
            it.setTitle("Muzyczka spauzowana byczq \uD83D\uDC4D")
        }
        else EmbedCreator {
            it.setTitle("Muzyczka wznawiona \uD83D\uDC4D")
        }
    }

    fun nowPlaying(): EmbedCreator {
        return player.playingTrack?.let { track ->

            val title = track.info.title
            val url = "https://www.youtube.com/watch?v=" + track.identifier
            val time = track.position.formatAsDuration() + "/" + track.duration.formatAsDuration()

            EmbedCreator {
                it.setTitle("Teraz leci:")
                it.setDescription("[$title]($url)\n" +
                        "${track.info.author}\n\n" +
                        "`" + time + "`\n" +
                        "Dodane przez `${track.songInfo.adder}`")
            }
        } ?: EmbedCreator {
            it.setTitle("Nic teraz nie leci :/")
        }
    }

    fun skip(): EmbedCreator {
        val skipped = scheduler.skip()

        return if (skipped)
            EmbedCreator {
                it.setTitle("Piosenka skipnięta szefie \uD83D\uDC4D")
            }
        else
            EmbedCreator {
                it.setTitle("Nie mam nic do skipnięcia byczq")
            }
    }

    fun queue(): EmbedCreator {
        return EmbedCreator {
            it.setTitle("Kłełe:")
            if (player.playingTrack == null) {
                it.setDescription("Nie mam nic w kłełe :(")
                return@EmbedCreator
            }

            val queue = listOf(player.playingTrack) + scheduler.getQueue()
            queue.forEachIndexed { index, track ->
                val prefix = if (index == 0) "Teraz: " else "$index. "
                it.addField(prefix + track.info.title,
                        "Dodane przez `${track.songInfo.adder}`",
                        false
                )
            }

            val length = scheduler.getQueue().sumBy { it.duration.toInt() }
            val currentLeft = player.playingTrack.duration - player.playingTrack.position
            val total = currentLeft + length
            it.addField("Pozostała długość:", total.formatAsDuration(), false)
        }
    }

}