package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.createEmbed
import me.kosert.vixiarz.send
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.File

object KiepscyHandler : IHandler {

    private const val FILE_NAME = "kiepscy.txt"

    private val randomQueue = mutableListOf<Episode>()

    init {
        val episodes = buildList {
            File(FILE_NAME).reader().useLines { lines ->
                lines.forEachIndexed { index, it ->
                    val (name, url) = it.split(" - http://")
                    add(Episode(name, "http://$url"))
                }
            }
        }
        randomQueue.addAll(episodes.shuffled())
    }

    override suspend fun handle(event: MessageReceivedEvent): Boolean {
        val episode = randomQueue.removeAt(0)
        randomQueue.add(episode)

        createEmbed {
            setTitle("Wylosowałem")
            setDescription(episode.toDescription())
        }.send(event.channel)

        return true
    }


    class Episode(
        val name: String,
        val url: String,
    ) {
        fun toDescription(): String = "$name\n[LINK]($url)"
    }
}