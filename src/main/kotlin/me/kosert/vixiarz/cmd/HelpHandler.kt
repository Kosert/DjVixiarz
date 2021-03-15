package me.kosert.vixiarz.cmd

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.rest.util.Color
import me.kosert.vixiarz.Const.FOOTER_TEXT
import me.kosert.vixiarz.Const.START_TAG
import me.kosert.vixiarz.channel

object HelpHandler : IHandler {

    override suspend fun handle(event: MessageCreateEvent): Boolean {
        event.channel()?.createEmbed {
            it.apply {
                setAuthor("DJ VIXIARZ", null, event.client.self.block()?.avatarUrl)
                setColor(Color.PINK)
                setDescription("Help:")
                Command.values().forEach {
                    addField(START_TAG + it.aliases.first(), it.help, true)
                }
                setFooter(FOOTER_TEXT, null)
            }
        }?.block()
        return true
    }
}