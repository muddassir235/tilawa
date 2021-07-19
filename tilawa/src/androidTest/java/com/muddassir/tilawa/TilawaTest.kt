package com.muddassir.tilawa

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val repetitions = 1

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

    private lateinit var tilawa: Tilawa
    private lateinit var qurraInfo: Array<QariInfo>

    /**
     * Test the following state transitions
     * stop -> start
     * start -> pause
     * pause -> start
     * pause -> stop
     * start -> stop
     * next
     * prev
     * seekTo(100*1000)
     * moveToIndex(2)
     * restart
     * changeUris
     */
    @Test
    @Repeat(repetitions)
    fun testTilawaStopStart() = afterTilawaStart {
        assertTrue(tilawa.changeState(stop))
        assertTrue(tilawa.changeState(start))
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaStartPause() = afterTilawaStart {
        assertTrue(tilawa.changeState(pause))
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaPauseStart() = afterTilawaStart {
        assertTrue(tilawa.changeState(pause))
        assertTrue(tilawa.changeState(start))
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaPauseStop() = afterTilawaStart {
        assertTrue(tilawa.changeState(pause))
        assertTrue(tilawa.changeState(stop))
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaStartStop() = afterTilawaStart {
        assertTrue(tilawa.changeState(stop))
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaNext() = afterTilawaStart {
        assertTrue(tilawa.changeState(next))
    }


    @Test
    @Repeat(repetitions)
    fun testTilawaPrev() = afterTilawaStart {
        assertTrue(tilawa.changeState(prev))
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaSeekTo() = afterTilawaStart {
        assertTrue(tilawa.changeState {
            seekTo(it, 100000)
        })
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaToIndex() = afterTilawaStart {
        assertTrue(tilawa.changeState {
            moveToIndex(it, 2)
        })
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaReciteFromStart() = afterTilawaStart {
        assertTrue(tilawa.changeState(reciteFromStart))
    }

    @Test
    @Repeat(repetitions)
    fun testTilawaChangeQari() = afterTilawaStart {
        assertTrue(tilawa.changeState {
            changeQari(it, qurraInfo[1], 0)
        })
    }

    private fun afterTilawaStart(task: (suspend (Unit)->Unit)) {
        runOnMainThread {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            tilawa = Tilawa(context)
            qurraInfo = getQurraData(context).value!!

            background {
                assertTrue(tilawa.setState(ExpectedTilawaState.defaultWithQariInfo(qurraInfo[0])))
                assertTrue(tilawa.changeState(start))
                assertTrue(tilawa.changeState(next))
                delay(5000)
                task.invoke(Unit)
            }
        }
    }

    private fun background( action:(suspend () -> Unit)) {
        GlobalScope.launch(Dispatchers.IO) {
            action()
        }
    }

    private fun runOnMainThread(task: ((Unit)->Unit)) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { task.invoke(Unit) }
        Thread.sleep(15000)
    }

    private fun runOnMainThread(duration: Long, task: ((Unit)->Unit)) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { task.invoke(Unit) }
        Thread.sleep(duration)
    }
}