package me.kosert.vixiarz.audio.youtube

class InnerTubeConfiguration(
    val INNERTUBE_API_KEY: String,
    val INNERTUBE_CLIENT_NAME: String,
    val INNERTUBE_CLIENT_VERSION: String,
    val STS: Int,
    val LOGGED_IN: Boolean,
) {

    companion object {
        fun default() = InnerTubeConfiguration(
            INNERTUBE_API_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8",
            INNERTUBE_CLIENT_NAME = "WEB",
            INNERTUBE_CLIENT_VERSION = "2.20220331.06.00",
            STS = 19082,
            LOGGED_IN = false,
        )
    }
}

