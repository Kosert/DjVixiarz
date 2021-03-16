package me.kosert.vixiarz.cmd

enum class Command(
        val aliases: List<String>,
        val help: String = "",
        vararg val handlers: IHandler
) {

    PING(listOf("ping"), "Pong", PingHandler),
    HELP(listOf("help", "?"), "Pomoc", HelpHandler),

    JOIN(listOf("join"), "Bot dołącza", JoinHandler),
    QUIT(listOf("quit", "leave"), "Bot wychodzi", RequireDjHandler, QuitHandler),

    PLAY(listOf("play", "p"), "Dodaje pioseneczke", PlayHandler),
    SKIP(listOf("skip"), "Skipuje pioseneczke", RequireDjHandler, SkipHandler),

    QUEUE(listOf("queue", "q"), "Wyświetla kłełe", QueueHandler),
    NOW(listOf("coleci", "np", "now"), "Wyświetla co leci", NowPlayingHandler),

    PAUSE(listOf("pause", "stop"), "Pauza", PauseHandler),
    RESUME(listOf("resume"), "Wznawia", ResumeHandler),


}