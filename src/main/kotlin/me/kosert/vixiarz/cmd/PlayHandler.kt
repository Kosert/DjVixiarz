package me.kosert.vixiarz.cmd

import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.rest.util.Color
import me.kosert.vixiarz.Const
import me.kosert.vixiarz.Const.ERROR_TITLE
import me.kosert.vixiarz.channel
import me.kosert.vixiarz.searcher.MusicSearcher
import me.kosert.vixiarz.voiceController

object PlayHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        val tokens = event.message.content.split(" ").filter { it.isNotBlank() }

        if (tokens.size < 2) {
            event.channel()?.sendError("Nie dałeś nutki gościu")
            return true
        }

        if (event.voiceController()?.isJoined() != true) {
            val joined = JoinHandler.handle(event)
            if (!joined)
                return false
        }

        val url = if (tokens[1].startsWith("http"))
            tokens[1]
        else {
            val query = tokens.drop(1).joinToString(" ")
            event.channel()?.createMessage("Szukam nutki: `$query`")?.block()

            val searched = MusicSearcher.search(query) ?: run {
                event.channel()?.sendError("Nic nie znalazłem :/")
                return true
            }

            event.channel()?.createMessage("Znalazłem: `${searched.title}`")?.block()
            searched.url
        }


        val adder = event.member.get()
        val embedCreator = event.voiceController()?.play(adder, url)
        event.channel()?.createEmbed { embedCreator?.create(it) }?.block()
        return true
    }

    private fun MessageChannel.sendError(text: String) {
        createEmbed {
            it.setTitle(ERROR_TITLE)
            it.setDescription(text)
            it.setColor(Color.PINK)
            it.setFooter(Const.FOOTER_TEXT, null)
        }?.block()
    }
}
