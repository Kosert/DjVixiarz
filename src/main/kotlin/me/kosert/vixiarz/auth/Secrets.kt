package me.kosert.vixiarz.auth

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import me.kosert.vixiarz.LOG
import java.io.File

private const val SECRET_FILENAME = "secret.json"

private class SecretJson(
    @SerializedName("discordToken")
    val token: String,
    @SerializedName("googleApiKey")
    val googleApiKey: String
)

object Secrets {

    private var token: String = ""
    private var googleApiKey: String = ""

    fun loadFromFile() {
        val json = File(SECRET_FILENAME).readText()
        val gson = Gson()
        val secrets = gson.fromJson(json, SecretJson::class.java)
        token = secrets.token
        googleApiKey = secrets.googleApiKey
        LOG.info("Secrets loaded")
    }

    fun getDiscordToken() = token
    fun getGoogleApiKey() = googleApiKey
}