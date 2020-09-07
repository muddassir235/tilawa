package com.muddassir.tilawa

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.muddassir.eprefs.load
import com.muddassir.eprefs.prefs
import com.muddassir.eprefs.save
import java.util.ArrayList

internal const val QURRA_DATA_KEY = "qurra_data"

/**
 * Get data of the Qurra available.
 *
 * @return Array of QariInfo objects which have the server urls of the qaris
 */
fun getQurraData(context: Context): MutableLiveData<Array<QariInfo>> {
    val qurraData: MutableLiveData<Array<QariInfo>> = MutableLiveData()

    qurraData.value = getSavedQurraData(context) ?: getHardCodedQurraData(context)

    QariServerUrlLoader(context).apply {
        urlsLoadListener = object: ServerUrlsLoadListener {
            override fun onQariUrlsLoaded(urls: Array<String?>?) {
                val currentVal = qurraData.value!!
                val data = Array(currentVal.size) {
                    QariInfo(
                        number = currentVal[it].number,
                        arabicName = currentVal[it].arabicName,
                        englishName = currentVal[it].englishName,
                        audioServerUrl = urls?.get(it) ?: currentVal[it].audioServerUrl,
                        availableSuvar = currentVal[it].availableSuvar,
                        isFavorite = currentVal[it].isFavorite
                    )
                }

                qurraData.value = data
                context.save(QURRA_DATA_KEY, qurraData)
            }
        }
    }

    return qurraData
}

internal val getSavedQurraData: ((Context) -> Array<QariInfo>?) = {
    val data = it.prefs.load<ArrayList<QariInfo>>(QURRA_DATA_KEY)
    data?.toTypedArray()
}

internal val getHardCodedQurraData: ((Context) -> Array<QariInfo>?) = {
    val qurraNames        = it.resources.getStringArray(R.array.qurra_names)
    val qurraEnglishNames = it.resources.getStringArray(R.array.qurra_english_names)
    val audioServerUrls: Array<String?>?  =
        it.resources.getStringArray(R.array.qurra_audio_servers)

    Array(qurraNames.size) { i ->
        QariInfo(
            number = i,
            arabicName = qurraNames[i],
            englishName = qurraEnglishNames[i],
            audioServerUrl = audioServerUrls?.get(i),
            isFavorite = false
        )
    }
}