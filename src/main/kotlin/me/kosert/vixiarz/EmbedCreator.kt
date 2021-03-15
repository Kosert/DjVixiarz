package me.kosert.vixiarz

import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import me.kosert.vixiarz.Const.FOOTER_TEXT

class EmbedCreator(
    private val creator: (EmbedCreateSpec) -> Unit
) {
    fun create(embedCreateSpec: EmbedCreateSpec) {
        embedCreateSpec.setColor(Color.PINK)
        embedCreateSpec.setFooter(FOOTER_TEXT, null)
        creator.invoke(embedCreateSpec)
    }
}