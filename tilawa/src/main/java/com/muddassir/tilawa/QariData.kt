package com.muddassir.tilawa

import android.content.Context

typealias QariInfoObserver = ((qariInfo : Array<QariInfo>) -> Unit)

fun loadQariData(observer: QariInfoObserver, context: Context) {
    val qariNames        = context.resources.getStringArray(R.array.qari_names)
    val qariEnglishNames = context.resources.getStringArray(R.array.qari_english_names)
    val audioServerUrls: Array<String?>?  =
        context.resources.getStringArray(R.array.reciter_audio_servers)

    observer.invoke(Array(qariNames.size) { i ->
        QariInfo(
            number = i,
            arabicName = qariNames[i],
            englishName = qariEnglishNames[i],
            audioServerUrl = audioServerUrls?.get(i),
            isFavorite = false
        )
    })
}