package com.muddassir.tilawa

import android.net.Uri
import com.google.android.exoplayer2.util.UriUtil
import com.muddassir.faudio.Audio
import com.muddassir.faudio.AudioObservation
import com.muddassir.faudio.AudioObserver
import com.muddassir.faudio.AudioStateInput
import java.io.Serializable

data class SurahInfo(val number        : Int    = -1,
                     val arabicName    : String = "",
                     val englishName   : String = "",
                     val makkiOrMadani : String = ""): Serializable

data class QariInfo(val number         : Int     = -1,
                    val arabicName     : String  = "",
                    val englishName    : String  = "",
                    val audioServerUrl : String? = "",
                    val availableSuvar : Array<SurahInfo> = SUVAR_INFO,
                    var isFavorite     : Boolean = false): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QariInfo

        if (number != other.number) return false
        if (arabicName != other.arabicName) return false
        if (englishName != other.englishName) return false
        if (audioServerUrl != other.audioServerUrl) return false
        if (!availableSuvar.contentEquals(other.availableSuvar)) return false
        if (isFavorite != other.isFavorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = number
        result = 31 * result + arabicName.hashCode()
        result = 31 * result + englishName.hashCode()
        result = 31 * result + (audioServerUrl?.hashCode() ?: 0)
        result = 31 * result + availableSuvar.contentHashCode()
        result = 31 * result + isFavorite.hashCode()
        return result
    }
}

data class TilawaObservation(val qariInfo: QariInfo? = null,
                             val surahInfo: SurahInfo? = null,
                             val audioStateInfo: AudioObservation? = null): Serializable

typealias TilawaObserver = ((observation: TilawaObservation) -> Unit)

class Tilawa(val qariInfo: QariInfo, val audio: Audio, val observers: HashSet<TilawaObserver>?) {
    val surahBeingRecited: SurahInfo get() = qariInfo.availableSuvar[audio.currentIndex]

    private val audioObserver: AudioObserver = { o ->
        observers?.forEach{
            it.invoke(
                TilawaObservation(
                    qariInfo, surahBeingRecited,
                    AudioObservation(
                        o.error, o.stopped, o.paused, o.index, o.progress,
                        o.bufferedPosition, o.duration
                    )
                )
            )
        }
    }

    init { this.audio.observers.add(audioObserver) }
}

val start: ((tilawa: Tilawa) -> Tilawa) = {
    Tilawa(
        it.qariInfo, Audio(
            it.audio,
            it.audio.context,
            qariInfoToAudioUrls(it.qariInfo),
            AudioStateInput(
                it.audio.currentIndex, false, it.audio.currentPosition,
                false
            )
        ), it.observers
    )
}

val pause: ((tilawa: Tilawa) -> Tilawa) = {
    Tilawa(
        it.qariInfo, Audio(
            it.audio,
            it.audio.context,
            qariInfoToAudioUrls(it.qariInfo),
            AudioStateInput(
                it.audio.currentIndex, true, it.audio.currentPosition,
                false
            )
        ), it.observers
    )
}

val stop: ((tilawa: Tilawa) -> Tilawa) = {
    Tilawa(
        it.qariInfo, Audio(
            it.audio,
            it.audio.context,
            qariInfoToAudioUrls(it.qariInfo),
            AudioStateInput(
                it.audio.currentIndex, !it.audio.started, it.audio.currentPosition,
                true
            )
        ), it.observers
    )
}

val next: ((tilawa: Tilawa) -> Tilawa) = {
    val possibleNextIndex = it.audio.currentIndex + 1
    val nextIndex = if (possibleNextIndex <= it.qariInfo.availableSuvar.lastIndex)
        possibleNextIndex else it.qariInfo.availableSuvar.lastIndex

    Tilawa(
        it.qariInfo, Audio(
            it.audio,
            it.audio.context,
            qariInfoToAudioUrls(it.qariInfo),
            AudioStateInput(
                nextIndex,
                !it.audio.started,
                0,
                it.audio.stopped
            )
        ), it.observers
    )
}

val previous: ((tilawa: Tilawa) -> Tilawa) = {
    val possiblePreviousIndex = it.audio.currentIndex - 1
    val previousIndex = if (possiblePreviousIndex >= 0) possiblePreviousIndex else 0

    Tilawa(
        it.qariInfo, Audio(
            it.audio,
            it.audio.context,
            qariInfoToAudioUrls(it.qariInfo),
            AudioStateInput(
                previousIndex,
                !it.audio.started,
                0,
                it.audio.stopped
            )
        ), it.observers
    )
}

val reciteFromStart: ((tilawa: Tilawa) -> Tilawa) = {
    Tilawa(
        it.qariInfo, Audio(
            it.audio,
            it.audio.context,
            qariInfoToAudioUrls(it.qariInfo),
            AudioStateInput(
                it.audio.currentIndex,
                !it.audio.started,
                0,
                it.audio.stopped
            )
        ), it.observers
    )
}

val reciteFrom =  { tilawa: Tilawa, millis: Long ->
    Tilawa(
        tilawa.qariInfo, Audio(
            tilawa.audio, tilawa.audio.context,
            qariInfoToAudioUrls(tilawa.qariInfo),
            AudioStateInput(
                tilawa.audio.currentIndex, !tilawa.audio.started, millis,
                false
            )
        ), tilawa.observers
    )
}

val changeSurah = { tilawa: Tilawa, surahInfo: SurahInfo ->
    Tilawa(
        tilawa.qariInfo, Audio(
            tilawa.audio, tilawa.audio.context,
            qariInfoToAudioUrls(tilawa.qariInfo),
            AudioStateInput(
                tilawa.qariInfo.availableSuvar.indexOf(surahInfo), !tilawa.audio.started,
                0, tilawa.audio.stopped
            )
        ), tilawa.observers
    )
}

val changeQari = { tilawa: Tilawa, qariInfo: QariInfo, defaultSurah: SurahInfo ->
    val surah = qariInfo.availableSuvar.find{ it.number == tilawa.surahBeingRecited.number }
        ?: defaultSurah

    Tilawa(
        qariInfo, Audio(
            tilawa.audio,
            tilawa.audio.context,
            qariInfoToAudioUrls(qariInfo),
            AudioStateInput(
                qariInfo.availableSuvar.indexOf(surah), !tilawa.audio.started,
                0, tilawa.audio.stopped
            )
        ), tilawa.observers
    )
}

val addObserver = { tilawa: Tilawa, observer: TilawaObserver ->
    val observers = tilawa.observers ?: HashSet()
    observers.add(observer)
    Tilawa(tilawa.qariInfo, tilawa.audio, observers)
}

val removeObservers = { tilawa: Tilawa ->
    tilawa.audio.release()

    Tilawa(
        tilawa.qariInfo,
        Audio(
            tilawa.audio,
            tilawa.audio.context,
            tilawa.audio.uris,
            AudioStateInput(
                tilawa.audio.currentIndex,
                !tilawa.audio.started,
                tilawa.audio.currentPosition,
                tilawa.audio.stopped
            )),
        null
    )
}

val qariInfoToAudioUrls: ((QariInfo) -> Array<Uri>) = { info ->
    info.availableSuvar.map {
        UriUtil.resolveToUri("${info.audioServerUrl}/",
            String.format("%03d", it.number+1)+".mp3") }.toTypedArray()
}

fun TilawaObservation.diff(from: TilawaObservation?): TilawaObservation {
    fun <T> diff(a: T, b: T) = if(a == b) null else b

    return TilawaObservation(
        diff(from?.qariInfo, this.qariInfo),
        diff(from?.surahInfo, this.surahInfo),
        AudioObservation(
            diff(from?.audioStateInfo?.error, this.audioStateInfo?.error),
            diff(from?.audioStateInfo?.stopped, this.audioStateInfo?.stopped),
            diff(from?.audioStateInfo?.paused, this.audioStateInfo?.paused),
            diff(from?.audioStateInfo?.index, this.audioStateInfo?.index),
            diff(from?.audioStateInfo?.progress, this.audioStateInfo?.progress),
            diff(from?.audioStateInfo?.bufferedPosition, this.audioStateInfo?.bufferedPosition),
            diff(from?.audioStateInfo?.duration, this.audioStateInfo?.duration)
        )
    )
}