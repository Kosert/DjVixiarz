package me.kosert.vixiarz

object FooterGenerator {

    private val footers = listOf<String>(
        "Weź kurwa zrób mi louda",
        "Naura",
        "Lubicie fetysz stóp?",
        "Moim zdaniem to jest przesada",
        "!play circus theme",
        "rush B, don't stop",
        "jebać psy i kochać pieski",
        "Nie jestem w szpitalu, a naszczałem do basenu",
        "\uD83E\uDD8D Harambe \uD83E\uDD8D duszno \uD83E\uDD8D nie mogę oddychać \uD83E\uDD8D",
        "życie jest jak granie w lola",
        "Panie Wiesławie, Pan mnie wjezie na burdel",
        "Alkohol jest niezdrowy, nie pijcie bo was zmiecie z planszy",
        "ŁAN HANDRED EEEEEEEEEEEEEEJTI",
        "Polski orzeł, biała rasa, chciałbym w ustach mieć kutasa",
        "Wypróbuj !Piotras",
        "Wypróbuj !dzungla",
        "Robson = Radek",
        "Zaczarowany burdel O O O",
        "Sejfy.pl - jesteśmy bezpieczni",
        "XES - od tyłu też jest fajnie",
        "Jacek, Ty jebany betoniarzu",
        "On mnie rucha, a ja jestem sucha",
        "Możesz odpalić fotki i iść se zwalić konia",
        "Pamiętasz jeszcze mordo co wysyłałeś wczoraj?",
        "Dlaczego Mati Murański nie może zjeść hamburgera?",
        "Ja jestem Fagata, sexy cipka spod Konina",
        "W kiepskim świecie kiepskie sprawy",

    )

    fun generate(): String = footers.random() + " ( ͡° ͜ʖ ͡°)"
}