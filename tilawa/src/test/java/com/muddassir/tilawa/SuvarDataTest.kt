package com.muddassir.tilawa

import org.junit.Test

import org.junit.Assert.*

class SuvarDataTest {
    @Test
    fun validNumberOfSurahs() {
        assertEquals(114, SUVAR_INFO.size)
    }

    @Test
    fun allValuesAreValid() {
        var somethingIsInvalid = false
        SUVAR_INFO.forEach {
            if(it.number !in 0..113) somethingIsInvalid = true
            if(it.arabicName.equals("") || it.englishName.equals("")) somethingIsInvalid = true
            if(it.makkiOrMadani !in arrayOf("makki", "madani"))       somethingIsInvalid = true
        }
        assertFalse(somethingIsInvalid)
    }
}