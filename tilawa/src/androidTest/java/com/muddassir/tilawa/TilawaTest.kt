package com.muddassir.tilawa

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.muddassir.faudio.Audio
import com.muddassir.faudio.AudioStateInput
import com.muddassir.kmacros.delay
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val repetitions = 3

/**
 * Instrumented test, which will execute on an Android device.
 *
 * These tests require the device to be connected to the internet and mp3quran.net to be accessible
 * from the device.
 */
@RunWith(AndroidJUnit4::class)
class TilawaTest {
    @Rule @JvmField
    var repeatRule: RepeatRule = RepeatRule()

    /**
     * Test the following state transitions
     *
     *
     * start       : stopped -> started
     * start       : paused -> started
     * pause       : started -> paused
     * stop        : started -> stopped
     * stop        : paused -> stopped
     * next        : surah 3 -> surah 4
     * previous    : surah 3 -> surah 2
     * previous    : surah 3 -> surah 2
     * reciteFrom  : _ -> 60 seconds
     * changeQari  : 0 -> 2
     * changeSurah : 3 -> 10
     */

    private lateinit var tilawa: Tilawa

    @After
    fun teardown() { tilawa.audio.release() /* A bit non-functional here :( */ }

    @Test
    @Repeat(repetitions)
    fun testStartStoppedToStarted() {
        afterTilawaStart {
            changeTilawa(stop)
            changeTilawa(start)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 20000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[3], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testStartPausedToStarted() {
        afterTilawaStart {
            changeTilawa(pause)
            changeTilawa(start)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 20000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[3], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testPauseStartedToPaused() {
        afterTilawaStart {
            changeTilawa(pause)
            observe {
                verifyTilawa(it, stopped = false, paused = true, progress = 10000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[3], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testStopStartedToStopped() {
        afterTilawaStart {
            changeTilawa(stop)
            observe {
                verifyTilawa(it, stopped = true, paused = true, progress = 10000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[3], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testStopPausedToStopped() {
        afterTilawaStart {
            changeTilawa(pause)
            changeTilawa(stop)
            observe {
                verifyTilawa(it, stopped = true, paused = true, progress = 10000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[3], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testNext() {
        afterTilawaStart {
            changeTilawa(next)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[4], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testPrev() {
        afterTilawaStart {
            changeTilawa(previous)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[2], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testReciteFrom() {
        afterTilawaStart {
            changeTilawa{ reciteFrom(it, 60000L) }
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 70000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[3], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testChangeQari(){
        afterTilawaStart {
            changeTilawa{
                changeQari(
                    it,
                    MOCK_QARI_INFO[2],
                    MOCK_QARI_INFO[2].availableSuvar[0]
                )
            }
            observeAfter(10000){
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[3], qariInfo = MOCK_QARI_INFO[2])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testChangeSurah(){
        afterTilawaStart {
            changeTilawa{
                changeSurah(
                    it,
                    MOCK_QARI_INFO[0].availableSuvar[10]
                )
            }
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[10], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testReciteFromStart(){
        afterTilawaStart {
            changeTilawa(reciteFromStart)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = MOCK_QARI_INFO[0].availableSuvar[3], qariInfo = MOCK_QARI_INFO[0])
            }
        }
    }

    private fun verifyTilawa(found: TilawaObservation, stopped:Boolean, paused:Boolean,
                             progress: Long, surahInfo: SurahInfo, qariInfo: QariInfo
    ) {
        assertEquals(found.audioStateInfo?.stopped?:true, stopped)
        assertEquals(found.audioStateInfo?.paused?:true, paused)
        assertEquals(found.audioStateInfo?.index?:0,
            qariInfo.availableSuvar.indexOf(surahInfo))
        assertTrue(found.audioStateInfo?.duration?:0L != 0L)
        assertTrue(found.audioStateInfo?.bufferedPosition?:0L != 0L)
        assertTrue((found.audioStateInfo?.progress ?: 0L) > (progress-10000L))
        assertTrue((found.audioStateInfo?.progress ?: 0L) < (progress+20000L))
        assertTrue((found.surahInfo ?: SUVAR_INFO[0]) == surahInfo)
        assertTrue((found.qariInfo?: MOCK_QARI_INFO[1]) == qariInfo)
    }

    private fun observeAfter(millis: Long, observer:
        (observation: TilawaObservation) -> Unit) {
        delay(millis) {
            observe(observer)
        }
    }

    private fun observe(observer: (observation: TilawaObservation) -> Unit) {
        tilawa = addObserver(tilawa, observer)
    }

    private fun changeTilawa(action: ((Tilawa) -> Tilawa)) {
        tilawa = action.invoke(tilawa)
    }

    private fun afterTilawaStart(task: ((Unit)->Unit)) {
        runOnMainThread {
            tilawa = start(getTilawaInStoppedState())
            delay(10000) { task.invoke(Unit) }
        }
    }

    private fun getTilawaInStoppedState(): Tilawa {
        return Tilawa(
            qariInfo = MOCK_QARI_INFO[0], audio = Audio(
                null,
                context = InstrumentationRegistry.getInstrumentation().targetContext,
                uris = qariInfoToAudioUrls(MOCK_QARI_INFO[0]),
                audioState = AudioStateInput(3, true, 0, true)
            ), observers = null
        )
    }

    private fun runOnMainThread(task: ((Unit)->Unit)) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { task.invoke(Unit) }
        Thread.sleep(30000)
    }
}