package me.kosert.vixiarz.cmd

import me.kosert.vixiarz.cmd.gif.JungleHandler
import me.kosert.vixiarz.cmd.gif.MetronomeHandler
import me.kosert.vixiarz.cmd.gif.PopeHandler
import me.kosert.vixiarz.cmd.gif.PusherHandler

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

    REMOVE(listOf("remove", "rm"), RequireDjHandler, RemoveHandler, help = "Usuwa piosenkę z kłełe"),
    UNDO(listOf("undo"), UndoHandler, help = "Cofa ostatnią dodaną piosenkę"),

    QUEUE(listOf("queue", "q"), QueueHandler, help = "Wyświetla kłełe"),
    NOW(listOf("coleci", "np", "now"), NowPlayingHandler, help = "Wyświetla co leci"),

    PAUSE(listOf("pause", "stop"), PauseHandler, help = "Pauza"),
    RESUME(listOf("resume"), ResumeHandler, help = "Wznawia"),
    SEEK(listOf("seek", "time"), RequireDjHandler, SeekHandler(), help = "Przewija do podanego momentu"),

    CLEAR(listOf("clear"), RequireDjHandler, ClearHandler(), help = "Czyści kłełe"),

//    SCHEDULE(listOf("ktogra"), ScheduleHandler, help = "Pokazuje harmonogram kotożerców"),
    RANDOM_KIEPSCY(listOf("losujodcinek", "losujkiepskich"), KiepscyHandler, help = "Podaje losowy odcinek Świata wg Kiepskich"),

//    LYRICS(listOf("lyrics"), LyricsHandler, help = "Pokazuje tekst piosenki"),

    URBAN_JUNGLE(listOf("miejskadżungla", "miejskadzungla", "dzungla"), JungleHandler),
    POPE(listOf("rarepope", "pope", "jp2"), PopeHandler),
    PUSHER(listOf("pusher", "babahassan"), PusherHandler),
    METRONOME(listOf("piotras", "metronome", "tiktok"), MetronomeHandler),

//    STAT(listOf("statsdump"), StatisticsCounter()),
    ALIAS(listOf("alias"), )

}