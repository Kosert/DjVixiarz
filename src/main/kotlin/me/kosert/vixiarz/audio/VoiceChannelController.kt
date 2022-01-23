package me.kosert.vixiarz.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.kosert.vixiarz.*
import me.kosert.vixiarz.Const.ERROR_TITLE
import me.kosert.vixiarz.audio.PlayerManager.PLAYER_MANAGER
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.VoiceChannel
import java.awt.Color
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class VoiceChannelController {

    private var currentChannel: VoiceChannel? = null

    private val player = PLAYER_MANAGER.createPlayer()
    private val scheduler = AudioTrackScheduler(player, ::handleException)
    private val audioHandler = AudioPlayerSendHandler(player)

    init {
        player.addListener(scheduler)
    }

    val currentSong: AudioTrack?
        get() = player.playingTrack

    fun isJoined() = currentChannel?.guild?.audioManager?.isConnected

    fun join(channel: VoiceChannel) {
        channel.guild.audioManager.openAudioConnection(channel)
        channel.guild.audioManager.sendingHandler = audioHandler
        currentChannel = channel
    }

    fun checkIfShouldLeave() {
        val connected = currentChannel?.members?.count() ?: return
        if (connected == 1) {
            LOG.info("Bot is alone, leaving")
            leave()
        }
    }

    fun leave() {
        scheduler.clear()
        currentChannel?.guild?.audioManager?.closeAudioConnection()
        currentChannel = null
    }

    private fun handleException(exception: Exception, track: AudioTrack?) {
        val channelId = track?.songInfo?.originChannelId
        val channel = currentChannel?.guild?.textChannels?.find { it.id == channelId } ?: return

        createEmbed {
            setTitle(ERROR_TITLE)
            setDescription("Dojebało Exception: $exception")
            exception.causesSequence().take(4).forEach {
                addField("Caused by", it.toString(), false)
            }
            setColor(Color.PINK)
            setFooter(FooterGenerator.generate(), null)
        }.send(channel)
    }

    suspend fun play(member: Member, url: String, originId: String): MessageEmbed {

        return suspendCoroutine { cont ->
            PLAYER_MANAGER.loadItemOrdered(this, url, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    scheduleTrack(track)

                    cont.resume(createEmbed {
                        setTitle("Dodaje:")
                        setDescription(track.info.title)
                        addField("Autor", track.info.author ?: "Nieznany", true)
                        val duration = if (track.info.isStream)
                            "NA ŻYWO"
                        else
                            track.info.length.formatAsDuration()
                        addField("Długość", duration, true)
                    })
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    playlist.selectedTrack?.let {
                        this.trackLoaded(it)
                        return
                    }

                    playlist.tracks.forEach {
                        scheduleTrack(it)
                    }

                    cont.resume(createEmbed {
                        setTitle("Dodaje ${playlist.tracks.size} piosenek")
                        setDescription(playlist.name)

                        playlist.tracks.take(3).forEach {
                            addField(it.info.title, it.info.author ?: "Nieznany", false)
                        }
                        val remaining = playlist.tracks.size - 3
                        if (remaining > 0) {
                            addField("... i jeszcze $remaining", "¯\\_(ツ)_/¯", true)
                        }

                        addField(
                            "Łączna długość",
                            playlist.tracks.sumOf { it.duration }.formatAsDuration(asText = true),
                            true
                        )
                    })
                }

                private fun scheduleTrack(track: AudioTrack) {
                    val user = member.user
                    track.userData = SongInfo(
                        "${user.name}#${user.discriminator}",
                        user.avatarUrl.orEmpty(),
                        originId
                    )
                    scheduler.play(track)
                }

                override fun noMatches() {
                    cont.resume(createEmbed {
                        setTitle(ERROR_TITLE)
                        setDescription("Nie znaleziono :/")
                    })
                }

                override fun loadFailed(exception: FriendlyException?) {
                    LOG.error("Dojebało exception", exception)
                    cont.resume(createEmbed {
                        setDescription("Dojebało exception: $exception")
                    })
                }
            })
        }
    }


    fun setPause(pause: Boolean): MessageEmbed? {
        if (player.isPaused == pause) {
            return null
        }

        scheduler.setPause(pause)
        return if (pause) createEmbed {
            setTitle("Muzyczka spauzowana byczq \uD83D\uDC4D")
        }
        else createEmbed {
            setTitle("Muzyczka wznowiona \uD83D\uDC4D")
        }
    }

    fun nowPlaying(): MessageEmbed {
        return player.playingTrack?.let { track ->

            val title = track.info.title
            val url = track.info.uri
            val time = if (track.info.isStream)
                "NA ŻYWO"
            else
                track.position.formatAsDuration() + "/" + track.duration.formatAsDuration()

            val header = if (player.isPaused)
                "Muzyka spauzowana - wpisz !resume żeby wznowić\n"
            else
                ""

            createEmbed {
                setTitle("Teraz leci:")
                setDescription(
                    header +
                            "[$title]($url)\n" +
                            "${track.info.author}\n\n" +
                            "`" + time + "`\n" +
                            "Dodane przez `${track.songInfo.adder}`"
                )
            }
        } ?: createEmbed {
            setTitle("Nic teraz nie leci :/")
        }
    }

    fun undo(issuerName: String): MessageEmbed {
        val index = scheduler.getQueue()
            .prepend(currentSong)
            .filterNotNull()
            .indexOfLast { track -> track.songInfo.adder == issuerName }

        return if (index >= 0) {
            remove(index)
        } else createEmbed {
            setTitle("Nie znalazłem żadnej twojej piosenki")
        }
    }

    fun remove(index: Int): MessageEmbed {
        if (index == 0) {
            return skip()
        }

        return scheduler.remove(index - 1)?.let { track ->
            createEmbed {
                setTitle("Usuwam:")
                setDescription(track.info.title)
            }
        } ?: run {
            createEmbed { setTitle("Nie mam piosenki pod numerem $index") }
        }
    }

    fun skip(): MessageEmbed {
        val skipped = scheduler.skip()

        return if (skipped)
            createEmbed { setTitle("Piosenka skipnięta szefie \uD83D\uDC4D") }
        else
            createEmbed { setTitle("Nie mam nic do skipnięcia byczq") }
    }

    fun clear(): MessageEmbed {
        scheduler.clear()
        return createEmbed { setTitle("Kłełe wyczyszczone") }
    }

    fun queue() = createEmbed {
        setTitle("Kłełe:")
        if (player.playingTrack == null) {
            setDescription("Nie mam nic w kłełe :(")
            return@createEmbed
        }

        if (player.isPaused) {
            setDescription("Muzyka spauzowana - wpisz !resume żeby wznowić")
        } else if (scheduler.getQueue().isNotEmpty()){
            setDescription("W kolejce jest ${scheduler.getQueue().size} piosenek")
        }

        val queue = listOf(player.playingTrack) + scheduler.getQueue()
        queue.take(21).forEachIndexed { index, track ->
            val prefix = if (index == 0) "Teraz: " else "$index. "
            addField(
                prefix + track.info.title,
                "Dodane przez `${track.songInfo.adder}`",
                false
            )
        }
        val remaining = queue.size - 21
        if (remaining > 0) {
            addField("... i jeszcze $remaining", "¯\\_(ツ)_/¯", true)
        }

        val length = scheduler.getQueue().sumOf { it.duration }
        val currentLeft = player.playingTrack.duration - player.playingTrack.position
        val total = currentLeft + length
        addField("Pozostała długość:", total.formatAsDuration(asText = true), false)
    }
}