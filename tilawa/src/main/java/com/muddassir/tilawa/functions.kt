package com.muddassir.tilawa

import android.net.Uri
import com.muddassir.faudio.ActualAudioState
import com.muddassir.faudio.ExpectedAudioState

val start: ((ActualTilawaState) -> ExpectedTilawaState) = {
    ExpectedTilawaState(
        qariInfo = it.qariInfo,
        recitationIndex = it.recitationIndex,
        surahIndex = it.surahIndex,
        paused = false,
        progress = it.progress,
        stopped = false
    )
}

val pause: ((ActualTilawaState) -> ExpectedTilawaState) = {
    ExpectedTilawaState(
        qariInfo = it.qariInfo,
        recitationIndex = it.recitationIndex,
        surahIndex = it.surahIndex,
        paused = true,
        progress = it.progress,
        stopped = false
    )
}

val stop: ((ActualTilawaState) -> ExpectedTilawaState) = {
    ExpectedTilawaState(
        qariInfo = it.qariInfo,
        recitationIndex = it.recitationIndex,
        surahIndex = it.surahIndex,
        paused = true,
        progress = 0,
        stopped = true
    )
}

val next: ((ActualTilawaState) -> ExpectedTilawaState) = {
    val nextIndex = (it.surahIndex+1)%it.qariInfo.recitations[it.recitationIndex].availableSuvar
        .size

    ExpectedTilawaState(
        qariInfo = it.qariInfo,
        recitationIndex = it.recitationIndex,
        surahIndex = nextIndex,
        paused = false,
        progress = 0,
        stopped = false
    )
}

val prev: ((ActualTilawaState) -> ExpectedTilawaState) = {
    val prevIndex = (it.surahIndex-1)%it.qariInfo.recitations[it.recitationIndex].availableSuvar
        .size

    ExpectedTilawaState(
        qariInfo = it.qariInfo,
        recitationIndex = it.recitationIndex,
        surahIndex = prevIndex,
        paused = false,
        progress = 0,
        stopped = false
    )
}

val moveToIndex = { currentState: ActualTilawaState, index: Int ->
    ExpectedTilawaState(
        qariInfo = currentState.qariInfo,
        recitationIndex = currentState.recitationIndex,
        surahIndex = index,
        paused = false,
        progress = 0,
        stopped = false
    )
}


val seekTo = { currentState: ActualTilawaState, millis: Long ->
    ExpectedTilawaState(
        qariInfo = currentState.qariInfo,
        recitationIndex = currentState.recitationIndex,
        surahIndex = currentState.surahIndex,
        paused = false,
        progress = millis,
        stopped = false
    )
}

val reciteFromStart: ((ActualTilawaState) -> ExpectedTilawaState) = {
    ExpectedTilawaState(
        qariInfo = it.qariInfo,
        recitationIndex = it.recitationIndex,
        surahIndex = it.surahIndex,
        paused = false,
        progress = 0,
        stopped = false
    )
}

val changeSurah = { currentState: ActualTilawaState, surahInfo: SurahInfo ->
    ExpectedTilawaState(
        qariInfo = currentState.qariInfo,
        recitationIndex = currentState.recitationIndex,
        surahIndex = currentState.qariInfo.recitations[currentState.recitationIndex].availableSuvar
            .indexOf(surahInfo),
        paused = false,
        progress = 0,
        stopped = false
    )
}

val changeRecitation = { currentState: ActualTilawaState, recitationInfo: RecitationInfo ->
    ExpectedTilawaState(
        qariInfo = currentState.qariInfo,
        recitationIndex = currentState.qariInfo.recitations.indexOf(recitationInfo),
        surahIndex = recitationInfo.availableSuvar.indexOf(currentState.surahInfo),
        paused = currentState.paused,
        progress = 0,
        stopped = false
    )
}

val changeQari = { currentState: ActualTilawaState, qariInfo: QariInfo, recitationIndex: Int ->
    ExpectedTilawaState(
        qariInfo = qariInfo,
        recitationIndex = recitationIndex,
        surahIndex = qariInfo.recitations[recitationIndex].availableSuvar
            .indexOf(currentState.surahInfo),
        paused = currentState.paused,
        progress = 0,
        stopped = false
    )
}

val reciteRandom: ((ActualTilawaState) -> ExpectedTilawaState) = {
    val randIndex = it.qariInfo.recitations[it.recitationIndex].availableSuvar.indices
        .shuffled().last()

    ExpectedTilawaState(
        qariInfo = it.qariInfo,
        recitationIndex = it.recitationIndex,
        surahIndex = randIndex,
        paused = false,
        progress = 0,
        stopped = false
    )
}

internal val recitationToAudioUrls: ((RecitationInfo) -> Array<Uri>) = { info ->
    info.availableSuvar.map {
        Uri.parse("${info.serverUrl}/" + String.format("%03d", it.number+1)+".mp3")
    }.toTypedArray()
}

internal val tilawaStateToAudioState: ((ExpectedTilawaState) -> ExpectedAudioState) = {
    ExpectedAudioState(
        recitationToAudioUrls(it.qariInfo.recitations[it.recitationIndex]),
        it.surahIndex,
        it.paused,
        it.progress,
        1.0f,
        it.stopped
    )
}

internal val actualTilawaStateToAudioState: ((ActualTilawaState) -> ActualAudioState) = {
    ActualAudioState(
        recitationToAudioUrls(it.qariInfo.recitations[it.recitationIndex]),
        it.surahIndex,
        it.paused,
        it.progress,
        1.0f,
        it.bufferedPosition,
        it.currentIndexDuration,
        it.stopped,
        it.error
    )
}

internal val audioStateToTilawaState = { qariInfo: QariInfo, actualState: ActualAudioState ->
    ActualTilawaState(
        qariInfo,
        qariInfo.recitations.indexOfFirst {
            actualState.uris.contentEquals(recitationToAudioUrls(it))
        },
        actualState.index,
        actualState.paused,
        actualState.progress,
        actualState.bufferedPosition,
        actualState.currentIndexDuration,
        actualState.stopped,
        actualState.error
    )
}
