package me.kosert.vixiarz

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

fun createEmbed(creator: EmbedBuilder.() -> Unit): MessageEmbed {
    return EmbedBuilder().apply {
        setColor(Color.PINK)
        setFooter(Const.FOOTER_TEXT, null)
        creator()
    }.build()
}