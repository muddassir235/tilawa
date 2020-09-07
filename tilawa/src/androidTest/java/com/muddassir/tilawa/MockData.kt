package com.muddassir.tilawa

val arabicNames = arrayOf(
    "الشيخ عبد الرحمن السديس",
    "الشيخ سعود بن ابراهيم الشريم",
    "الشيخ صلاح بن محمد البدير",
    "الشيخ علي بن عبد الرحمن الحذيفي",
    "الشيخ ماهر بن حمد المعيقلي",
    "الشيخ عبد الله عواد الجهني",
    "الشيخ محمد اللحيدان",
    "الشيخ مشاري بن راشد العفاسي",
    "الشيخ علي جابر",
    "الشيخ بندر بليله",
    "الشيخ عبـد الباسـط عبـد ٱلصـمـد",
    "الشيخ رعد محمد الكردی",
    "الشيخ أحمد بن علي العجمي"
)

val englishNames = arrayOf(
    "Abdur Rahman as Sudais",
    "Saud bin Ibrahim ash Shuraim",
    "Salah bin Muhammad al Budair",
    "Ali bin Abdur Rahman al Hudhaify",
    "Shiekh Maher al Mu\'aiqly",
    "Abdullah \'awad Al Juhainy",
    "Muhammad al Luhaidan",
    "Mishary bin Rashid al Afasy",
    "Ali Jaber",
    "Bandar Baleela",
    "Abdul Basit Abdus Samad",
    "Raad Muhammad al Kurdi",
    "Ahmed bin Ali al \'Ajmi"
)

val serverUrls = arrayOf(
    "https://server11.mp3quran.net/sds",
    "https://server7.mp3quran.net/shur",
    "https://server6.mp3quran.net/s_bud",
    "https://server9.mp3quran.net/hthfi",
    "https://server12.mp3quran.net/maher",
    "https://server13.mp3quran.net/jhn",
    "https://server8.mp3quran.net/lhdan",
    "https://server8.mp3quran.net/afs",
    "https://server11.mp3quran.net/a_jbr",
    "https://server6.mp3quran.net/balilah",
    "https://server7.mp3quran.net/basit",
    "https://server6.mp3quran.net/kurdi",
    "https://server10.mp3quran.net/ajm/128"
)

val MOCK_QARI_INFO = Array(arabicNames.size) { i ->
    com.muddassir.tilawa.QariInfo(
        number = i,
        arabicName = arabicNames[i],
        englishName = englishNames[i],
        audioServerUrl = serverUrls[i],
        isFavorite = false
    )
}