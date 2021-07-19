package com.muddassir.tilawa

//import android.content.Context
//import androidx.lifecycle.MutableLiveData
//import com.muddassir.eprefs.delete
//import com.muddassir.eprefs.load
//import com.muddassir.eprefs.save
//import com.muddassir.faudio.*
//
//internal const val TILAWA_OBSERVATION_KEY = "tilawa_observation"
//
//class TilawaProducer(val context: Context) {
//    private val qurraLiveData: MutableLiveData<Array<QariInfo>> = getQurraData(context)
//    private val lastTilawaObservation: TilawaObservation?
//        get() = context.load(TILAWA_OBSERVATION_KEY)
//
//    /* Information of the available Qurra */
//    val qurraInfo: Array<QariInfo> get() = qurraLiveData.value!!
//
//    private val ap = AudioProducerBuilder(context).build()
//
//    private var tilawa: Tilawa = Tilawa(
//        qariInfo = lastTilawaObservation?.qariInfo ?: qurraInfo[0],
//        audio = Audio(
//            ap = ap,
//            context = context,
//            uris = qariInfoToAudioUrls(lastTilawaObservation?.qariInfo ?: qurraInfo[0]),
//            audioState = AudioStateInput(
//                index = lastTilawaObservation?.audioStateInfo?.index ?: 0,
//                paused = true /* Keep tilawa paused when app gets restarted */,
//                progress = lastTilawaObservation?.audioStateInfo?.progress ?: 0L,
//                stopped = lastTilawaObservation?.audioStateInfo?.stopped ?: true
//            )),
//        observers = null
//    )
//
//    init {
//        tilawa = addObserver(tilawa) {
//            context.save(TILAWA_OBSERVATION_KEY, it)
//        }
//    }
//
//    /* Perform an action on the tilawa e.g. start, pause, stop, changeSurah, changeQari e.t.c */
//    fun act(action: ((Tilawa) -> Tilawa)) {
//        tilawa = action.invoke(tilawa)
//    }
//
//    /* Reset saved state will be set qari 0, surah 0, progress 0, paused and stopped */
//    fun resetState() { context.delete<TilawaObservation>(TILAWA_OBSERVATION_KEY) }
//
//    /* Current state of the tilawa */
//    fun observation(): TilawaObservation {
//        return TilawaObservation(tilawa.qariInfo, tilawa.surahBeingRecited, AudioObservation(
//            tilawa.audio.error,
//            tilawa.audio.stopped,
//            !tilawa.audio.started,
//            tilawa.audio.currentIndex,
//            tilawa.audio.currentPosition,
//            null,
//            null
//        ))
//    }
//
//    /* Observe state changes and get a diff of the changes that happened. */
//    fun addTilawaDiffObserver(observer: ((prev: TilawaObservation?, now: TilawaObservation,
//                                          diff: TilawaObservation) -> Unit)) {
//        var prev: TilawaObservation? = null
//
//        act {
//            addObserver(it) { now ->
//                observer.invoke(prev, now, now.diff(prev))
//                prev = now
//            }
//        }
//    }
//}