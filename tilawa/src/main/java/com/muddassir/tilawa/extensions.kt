package com.muddassir.tilawa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.muddassir.faudio.AudioStateChangeTypes
import com.muddassir.faudio.changeType

val Tilawa.stateDiff: LiveData<TilawaStateDiff>
    get() {
        var prev: ActualTilawaState? = null
        val diffLd = MediatorLiveData<TilawaStateDiff>()

        diffLd.addSource(state) {
            diffLd.value = TilawaStateDiff(prev, it, prev?.audioStateChange(it))

            prev = it
        }

        return diffLd
    }

fun <X, Y> LiveData<X>.map(function: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, function)
}

fun <X, Y> LiveData<X>.switchMap(function: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, function)
}

class TilawaStateChangeTypes {
    companion object {
        const val START = "start"
        const val PAUSE = "pause"
        const val STOP  = "stop"
        const val NEXT  = "next"
        const val PREV  = "prev"
        const val SEEK  = "seek"
        const val CHANGE_SURAH = "change_SURAH"
        const val RECITE_FROM_START = "recite_from_start"
        const val QARI_CHANGED = "qari_changed"
        const val RECITATION_CHANGED = "recitation_changed"
        const val UNCHANGED = "unchanged"
        const val UNKNOWN = "unknown"
    }
}

data class TilawaStateDiff(
    val prev: ActualTilawaState?,
    val next: ActualTilawaState,
    val audioStateChangeKey: String?
)

fun ActualTilawaState.audioStateChange(next: ActualTilawaState): String {
    val currentAudioState = actualTilawaStateToAudioState(this)
    val nextAudioState = actualTilawaStateToAudioState(next)

    return when(currentAudioState.changeType(nextAudioState)) {
        AudioStateChangeTypes.START         -> TilawaStateChangeTypes.START
        AudioStateChangeTypes.PAUSE         -> TilawaStateChangeTypes.PAUSE
        AudioStateChangeTypes.STOP          -> TilawaStateChangeTypes.STOP
        AudioStateChangeTypes.NEXT          -> TilawaStateChangeTypes.NEXT
        AudioStateChangeTypes.PREV          -> TilawaStateChangeTypes.PREV
        AudioStateChangeTypes.SEEK          -> TilawaStateChangeTypes.SEEK
        AudioStateChangeTypes.MOVE_TO_INDEX -> TilawaStateChangeTypes.CHANGE_SURAH
        AudioStateChangeTypes.RESTART       -> TilawaStateChangeTypes.RECITE_FROM_START
        AudioStateChangeTypes.URIS_CHANGED  -> {
            if(this.qariInfo == next.qariInfo) {
                TilawaStateChangeTypes.RECITATION_CHANGED
            } else{
                TilawaStateChangeTypes.QARI_CHANGED
            }
        }
        AudioStateChangeTypes.UNCHANGED     -> TilawaStateChangeTypes.UNCHANGED
        AudioStateChangeTypes.UNKNOWN       -> TilawaStateChangeTypes.UNKNOWN
        else -> TilawaStateChangeTypes.UNKNOWN
    }
}