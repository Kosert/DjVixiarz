package me.kosert.vixiarz.models

import kotlinx.datetime.toKotlinLocalDateTime
import me.kosert.vixiarz.LOG
import me.sargunvohra.lib.ktunits.hours
import me.sargunvohra.lib.ktunits.minutes
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import kotlinx.datetime.LocalDateTime as KotlinLocalDateTime

import java.time.format.DateTimeFormatter

sealed class Presence(val isPlaying: Boolean) {

    abstract val name: String
    abstract val timestamp: KotlinLocalDateTime

    abstract fun generateEmbedRow(): String

    data class Accepted(
        override val timestamp: KotlinLocalDateTime,
        override val name: String,
        val startTime: Time,
        val endTime: Time?
    ): Presence(true) {

        override fun generateEmbedRow() = buildString {
            append("$name:".padEnd(9))
            for(it in 16..23) {
                append("|${forHour(it)}")
            }
        }

        private fun forHour(hour: Int): String {
            val thisHour = Time(hour, 0, 0)
            val nextHour = (thisHour + 1.hours).takeIf { it > thisHour } ?: Time(23, 59, 59)
            val endTime = when {
                endTime == null -> Time(23, 59, 59)
                endTime < startTime -> Time(23, 59, 59)
                else -> endTime
            }

            if (startTime >= nextHour)
                return "...."

            return buildString {
                repeat(4) {
                    val time = (thisHour + (4 * 15).minutes).takeIf { it >= thisHour } ?: Time(23, 59, 59)
                    if (time in startTime..endTime)
                        append("=")
                    else
                        append(".")
                }
            }
        }
    }

    data class Denied(
        override val timestamp: KotlinLocalDateTime,
        override val name: String,
        val reason: String
    ): Presence(false) {
        override fun generateEmbedRow(): String = "$name nie gra, powód: $reason"
    }

    companion object {

        private val datePattern by lazy {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        }

        fun fromCsvRow(row: String) = kotlin.runCatching {
            val items = tokenize(row)
            val timestamp = LocalDateTime.parse(items[0], datePattern).toKotlinLocalDateTime()
            when (items[2]) {
                "Tak" -> Accepted(
                    timestamp,
                    name = items[1],
                    startTime = Time(timeString = items[3]),
                    endTime = items[4].takeIf { it.isNotBlank() }?.let { Time(timeString = it) }
                )
                "Nie" -> Denied(
                    timestamp,
                    name = items[1],
                    items[5]
                )
                else -> throw IllegalArgumentException("Unresolved boolean value: ${items[2]}")
            }
        }.getOrElse {
            LOG.error("Failed to parse Presence CSV row: $row", it)
            null
        }

        /*
        *   Split by comma, but ignore commas in quotes
        */
        private fun tokenize(record: String): List<String> {
            return record.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*\$)".toRegex())
        }
    }
}