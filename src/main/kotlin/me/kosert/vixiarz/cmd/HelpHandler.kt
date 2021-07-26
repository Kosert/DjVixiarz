package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.Const.FOOTER_TEXT
import me.kosert.vixiarz.Const.START_TAG
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color

object HelpHandler : IHandler {

    override suspend fun handle(event: MessageReceivedEvent): Boolean {

        val embed = EmbedBuilder().apply {
            setAuthor("DJ VIXIARZ", null, event.jda.selfUser.avatarUrl)
            setColor(Color.PINK)
            setDescription("Help:")
            Command.values().forEach { cmd ->
                val adminRequired = cmd.handlers.any { it is RequireDjHandler }
                cmd.help?.let {
                    val postfix = (if (adminRequired) " (*)" else "")
                    addField(START_TAG + cmd.aliases.first() + postfix, it, true)
                }
            }
            addField("(*) - Komenda wymaga rangi 'DJ'", "", false)
            setFooter(FOOTER_TEXT, null)
        }

        event.channel.sendMessage(embed.build()).complete()
        return true
    }
}