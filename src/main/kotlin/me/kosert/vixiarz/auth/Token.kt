package me.kosert.vixiarz.auth

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import me.kosert.vixiarz.LOG
import java.io.File

private const val SECRET_FILENAME = "secret.json"

private class SecretJson(
    @SerializedName("discordToken")
    val token: String
)

object Token {

    private var token: String = ""

    fun loadFromFile() {
        val json = File(SECRET_FILENAME).readText()
        val gson = Gson()
        token = gson.fromJson(json, SecretJson::class.java).token
        LOG.info("Token loaded")
    }

    fun get() = token

}