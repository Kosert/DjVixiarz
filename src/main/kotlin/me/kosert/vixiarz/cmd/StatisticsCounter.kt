package me.kosert.vixiarz.cmd

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.kosert.vixiarz.createEmbed
import me.kosert.vixiarz.send
import me.kosert.vixiarz.sendError
import me.kosert.vixiarz.voiceController
import me.sargunvohra.lib.ktunits.minutes
import me.sargunvohra.lib.ktunits.seconds
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class StatisticsCounter : IHandler {

    data class Item(val author: User, val song: String)

    override suspend fun handle(event: MessageReceivedEvent): Boolean {

        var parsedCount = 0
        val results = mutableListOf<Item>()

        withContext(Dispatchers.IO) {

            val token = event.message.contentRaw.split(" ")[1]
            val channel = event.channel.jda.getGuildChannelById(token) as? TextChannel
            println("Channel: $channel")
            channel ?: return@withContext

            var msgs = channel.getHistoryFromBeginning(100).complete()

            while (true) {
                if (msgs.retrievedHistory.isEmpty()) {
                    //todo return
                    break
                }

//                msgs.retrievedHistory.forEach {
//                    if (!it.contentRaw.startsWith("!p ") && !it.contentRaw.startsWith("!play "))
//                        return@forEach
//
//                    results.add(Item(it.author, it.contentRaw))
//                    println("X: ${it.id} | ${it.timeCreated} | ${it.author} | ${it.contentRaw}")
//                }
                msgs.retrievedHistory.reversed().forEach {
                    if (!it.author.name.contains("DJ VIXIARZ") || it.embeds.firstOrNull()?.title?.contains("Dodaje") != true)
                        return@forEach

                    println("X: ${it.id} | ${it.timeCreated} | ${it.author} | ${it.embeds.first().description}")
                }

                parsedCount += msgs.retrievedHistory.size
                println("Parsed: $parsedCount | Found ${results.size} | last: ${msgs.retrievedHistory.last().timeCreated}")

                msgs = channel.getHistoryAfter(msgs.retrievedHistory.first(), 100).complete()
            }

        }

        println("Finished")
        return true
    }
}