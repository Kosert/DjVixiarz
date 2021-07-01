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
                cmd.help?.let {
                    addField(START_TAG + cmd.aliases.first(), it, true)
                }
            }
            setFooter(FOOTER_TEXT, null)
        }

        event.channel.sendMessage(embed.build()).complete()
        return true
    }
}