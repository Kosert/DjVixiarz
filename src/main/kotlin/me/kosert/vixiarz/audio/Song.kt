package me.kosert.vixiarz.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class SongInfo(
    val adder: String,
    val adderAvatarUrl: String
    //val imgUrl: String
)

val AudioTrack.songInfo: SongInfo
    get() = this.getUserData(SongInfo::class.java)