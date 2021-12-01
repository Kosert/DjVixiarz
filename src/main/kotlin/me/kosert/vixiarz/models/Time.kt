package me.kosert.vixiarz.models

import me.kosert.vixiarz.addIf
import me.sargunvohra.lib.ktunits.TimeValue
import me.sargunvohra.lib.ktunits.hours
import me.sargunvohra.lib.ktunits.minutes
import me.sargunvohra.lib.ktunits.seconds
import kotlin.math.absoluteValue

class Time private constructor(
    val hour: Int,
    val minute: Int,
    val second: Int
) : Comparable<Time> {
    val timeString: String
        get() {
            val m = String.format("%02d", minute)
            val s = String.format("%02d", second)
            return "$hour:$m:$s"
        }

    operator fun plus(timeValue: TimeValue): Time {
        if (timeValue.toMilliseconds < 0)
            return minus(timeValue)

        val secondsToAdd = timeValue.toSeconds

        val newSeconds = (second + secondsToAdd) % 60
        val minutesOverFlow = (second + secondsToAdd).seconds.toMinutes

        val newMinutes = (minute + minutesOverFlow) % 60
        val hourOverflow = (minute + minutesOverFlow).minutes.toHours

        val newHours = (hour + hourOverflow) % 24
        return Time(newHours, newMinutes, newSeconds)
    }

    operator fun minus(timeValue: TimeValue): Time {
        val secondsToSubstract = timeValue.toSeconds.absoluteValue

        val newSeconds = (60 + (second - (secondsToSubstract % 60))) % 60
        val minutesOverFlow = secondsToSubstract.seconds.toMinutes.addIf(1) {
            secondsToSubstract > second
        }

        val newMinutes = (60 + (minute - (minutesOverFlow % 60))) % 60
        val hourOverflow = minutesOverFlow.minutes.toHours.addIf(1) {
            minutesOverFlow > minute
        }

        val newHours = (24 + (hour - hourOverflow)) % 24
        return Time(newHours, newMinutes, newSeconds)
    }

    override fun hashCode(): Int {
        return timeString.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Time
        return timeString == other.timeString
    }

    override fun toString(): String {
        return "Time($timeString)"
    }

    override fun compareTo(other: Time): Int {
        fun Time.abs() = (hour.hours.toSeconds + minute.minutes.toSeconds + second).toInt()
        return this.abs() - other.abs()
    }

    companion object Factory {

        operator fun invoke(hour: Int, minute: Int, second: Int) = Time(hour, minute, second)

        operator fun invoke(hour: Long, minute: Long, second: Long) = Time(
            hour.toInt(),
            minute.toInt(),
            second.toInt()
        )

        operator fun invoke(timeString: String): Time {
            val items = timeString.split(":")
            return Time(
                items[0].toInt() % 24,
                items[1].toIntOrNull() ?: 0,
                items[2].toIntOrNull() ?: 0
            )
        }
    }
}