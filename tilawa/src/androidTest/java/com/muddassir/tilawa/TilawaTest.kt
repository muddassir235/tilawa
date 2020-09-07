package com.muddassir.tilawa

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.muddassir.kmacros.delay
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
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
     * start           : stopped -> started
     * start           : paused -> started
     * pause           : started -> paused
     * stop            : started -> stopped
     * stop            : paused -> stopped
     * next            : surah 3 -> surah 4
     * previous        : surah 3 -> surah 2
     * previous        : surah 3 -> surah 2
     * reciteFrom      : _ -> 60 seconds
     * changeQari      : 0 -> 2
     * changeSurah     : 3 -> 10
     * reciteFromStart : _ -> 0 seconds
     */

    private lateinit var tilawaProducer: TilawaProducer

    @After
    fun teardown() {
        try {
            tilawaProducer.act(removeObservers)
        } catch (e: Exception) {

        }
    }

    @Test
    @Repeat(repetitions)
    fun testStartStoppedToStarted() {
        afterTilawaStart {
            tilawaProducer.act(stop)
            tilawaProducer.act(start)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 20000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testStartPausedToStarted() {
        afterTilawaStart {
            tilawaProducer.act(pause)
            tilawaProducer.act(start)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 20000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testPauseStartedToPaused() {
        afterTilawaStart {
            tilawaProducer.act(pause)
            observe {
                verifyTilawa(it, stopped = false, paused = true, progress = 10000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testStopStartedToStopped() {
        afterTilawaStart {
            tilawaProducer.act(stop)
            observe {
                verifyTilawa(it, stopped = true, paused = true, progress = 10000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testStopPausedToStopped() {
        afterTilawaStart {
            tilawaProducer.act(pause)
            tilawaProducer.act(stop)
            observe {
                verifyTilawa(it, stopped = true, paused = true, progress = 10000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testNext() {
        afterTilawaStart {
            tilawaProducer.act(next)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[4],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testPrev() {
        afterTilawaStart {
            tilawaProducer.act(previous)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[2],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testReciteFrom() {
        afterTilawaStart {
            tilawaProducer.act{ reciteFrom(it, 60000L) }
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 70000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testChangeQari(){
        afterTilawaStart {
            tilawaProducer.act {
                changeQari(
                    it,
                    tilawaProducer.qurraInfo[2],
                    tilawaProducer.qurraInfo[2].availableSuvar[0]
                )
            }
            observeAfter(10000){
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                    qariInfo = tilawaProducer.qurraInfo[2])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testChangeSurah(){
        afterTilawaStart {
            tilawaProducer.act{
                changeSurah(
                    it,
                    tilawaProducer.qurraInfo[0].availableSuvar[10]
                )
            }
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[10],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testReciteFromStart(){
        afterTilawaStart {
            tilawaProducer.act(reciteFromStart)
            observeAfter(10000) {
                verifyTilawa(it, stopped = false, paused = false, progress = 10000L,
                    surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                    qariInfo = tilawaProducer.qurraInfo[0])
            }
        }
    }

    @Test
    @Repeat(repetitions)
    fun testStatePersistence(){
        afterTilawaStart(150000L) {
            delay(120000L) {
                tilawaProducer = TilawaProducer(
                    InstrumentationRegistry.getInstrumentation().targetContext)

                tilawaProducer.act(start)

                observeAfter(10000L) {
                    verifyTilawa(it, stopped = false, paused = false, progress = 130000L,
                        surahInfo = tilawaProducer.qurraInfo[0].availableSuvar[3],
                        qariInfo = tilawaProducer.qurraInfo[0])
                }
            }
        }
    }

    private fun verifyTilawa(found: TilawaObservation, stopped:Boolean, paused:Boolean,
                             progress: Long, surahInfo: SurahInfo, qariInfo: QariInfo
    ) {
        assertEquals(stopped, found.audioStateInfo?.stopped?:true)
        assertEquals(paused, found.audioStateInfo?.paused?:true)
        assertEquals(qariInfo.availableSuvar.indexOf(surahInfo),
            found.audioStateInfo?.index?:0)
        assertTrue(found.audioStateInfo?.duration?:0L != 0L)
        assertTrue(found.audioStateInfo?.bufferedPosition?:0L != 0L)
        assertTrue((found.audioStateInfo?.progress ?: 0L) > (progress-10000L))
        assertTrue((found.audioStateInfo?.progress ?: 0L) < (progress+20000L))
        assertEquals(surahInfo, found.surahInfo ?: SUVAR_INFO[0])
        assertEquals(qariInfo, found.qariInfo)
    }

    private fun observeAfter(millis: Long, observer:
        (observation: TilawaObservation) -> Unit) {
        delay(millis) {
            observe(observer)
        }
    }

    private fun observe(observer: (observation: TilawaObservation) -> Unit) {
        tilawaProducer.act{ addObserver(it, observer) }
    }

    private fun afterTilawaStart(task: ((Unit)->Unit)) {
        runOnMainThread {
            tilawaProducer = TilawaProducer(
                context = InstrumentationRegistry.getInstrumentation().targetContext)
            tilawaProducer.act{ changeSurah(it, SUVAR_INFO[3]) }
            tilawaProducer.act(start)
            delay(10000) { task.invoke(Unit) }
        }
    }

    private fun afterTilawaStart(duration: Long, task: ((Unit)->Unit)) {
        runOnMainThread(duration) {
            tilawaProducer = TilawaProducer(
                context = InstrumentationRegistry.getInstrumentation().targetContext)
            tilawaProducer.act{ changeSurah(it, SUVAR_INFO[3]) }
            tilawaProducer.act(start)
            delay(10000) { task.invoke(Unit) }
        }
    }

    private fun runOnMainThread(task: ((Unit)->Unit)) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { task.invoke(Unit) }
        Thread.sleep(30000)
    }

    private fun runOnMainThread(duration: Long, task: ((Unit)->Unit)) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { task.invoke(Unit) }
        Thread.sleep(duration)
    }
}