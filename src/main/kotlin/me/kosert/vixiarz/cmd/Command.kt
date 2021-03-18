package me.kosert.vixiarz.cmd

enum class Command(
    val aliases: List<String>,
    vararg val handlers: IHandler,
    val help: String? = null
) {

    PING(listOf("ping"), PingHandler, help = "Pong"),
    HELP(listOf("help", "?"), HelpHandler, help = "Pomoc"),

    JOIN(listOf("join"), JoinHandler, help = "Bot dołącza"),
    QUIT(listOf("quit", "leave"), RequireDjHandler, QuitHandler, help = "Bot wychodzi"),

    PLAY(listOf("play", "p"), PlayHandler, help = "Dodaje pioseneczke"),
    SKIP(listOf("skip"), RequireDjHandler, SkipHandler, help = "Skipuje pioseneczke"),

    QUEUE(listOf("queue", "q"), QueueHandler, help = "Wyświetla kłełe"),
    NOW(listOf("coleci", "np", "now"), NowPlayingHandler, help = "Wyświetla co leci"),

    PAUSE(listOf("pause", "stop"), PauseHandler, help = "Pauza"),
    RESUME(listOf("resume"), ResumeHandler, help = "Wznawia"),

    URBAN_JUNGLE(listOf("miejskadżungla", "miejskadzungla", "dzungla"), JungleHandler)

}