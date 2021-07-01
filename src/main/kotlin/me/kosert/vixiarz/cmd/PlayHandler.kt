package me.kosert.vixiarz.cmd

import kotlinx.coroutines.*
import me.kosert.vixiarz.Const
import me.kosert.vixiarz.Const.ERROR_TITLE
import me.kosert.vixiarz.createEmbed
import me.kosert.vixiarz.searcher.MusicSearcher
import me.kosert.vixiarz.sendError
import me.kosert.vixiarz.voiceController
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import kotlin.coroutines.coroutineContext

object PlayHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean = coroutineScope {
        val tokens = event.message.contentRaw.split(" ").filter { it.isNotBlank() }

        if (tokens.size < 2) {
            event.channel.sendError("Nie dałeś nutki gościu")
            return@coroutineScope true
        }

        if (event.voiceController()?.isJoined() != true) {
            val joined = JoinHandler.handle(event)
            if (!joined)
                return@coroutineScope false
        }

        val url = if (tokens[1].startsWith("http"))
            tokens[1]
        else {
            val query = tokens.drop(1).joinToString(" ")

            launch(Dispatchers.IO) {
                event.channel.sendMessage("Szukam nutki: `$query`").complete()
            }

            val searched = MusicSearcher.search(query) ?: run {
                event.channel.sendError("Nic nie znalazłem :/")
                return@coroutineScope true
            }

            launch(Dispatchers.IO) {
                event.channel.sendMessage("Znalazłem: `${searched.title}`").complete()
            }
            searched.url
        }

        val adder = event.member ?: return@coroutineScope true
        val embedResult = event.voiceController().play(adder, url)
        event.channel.sendMessage(embedResult).complete()
        return@coroutineScope true
    }
}
