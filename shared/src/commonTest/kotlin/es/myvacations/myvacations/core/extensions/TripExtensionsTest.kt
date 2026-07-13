package es.myvacations.myvacations.core.extensions

import kotlin.test.Test
import kotlin.test.assertEquals

class TripExtensionsTest {

    @Test
    fun roundTo2Decimals_roundsCorrectly() {
        assertEquals(10.12, 10.123.roundTo2Decimals())
        assertEquals(10.13, 10.126.roundTo2Decimals())
        assertEquals(10.0, 10.0.roundTo2Decimals())
        assertEquals(-10.12, (-10.123).roundTo2Decimals())
    }

    @Test
    fun roundTo1Decimals_roundsCorrectly() {
        assertEquals(10.1, 10.12.roundTo1Decimals())
        assertEquals(10.2, 10.16.roundTo1Decimals())
        assertEquals(10.0, 10.0.roundTo1Decimals())
        assertEquals(-10.1, (-10.12).roundTo1Decimals())
    }

    @Test
    fun transformInInitials_returnsCorrectInitials() {
        assertEquals("JD", "John Doe".transformInInitials())
        assertEquals("J", "John".transformInInitials())
        assertEquals("JD", "john doe".transformInInitials())
        assertEquals("JD", "  John   Doe  ".transformInInitials())
        assertEquals("JD", "John Doe Smith".transformInInitials())
        assertEquals("", "".transformInInitials())
        assertEquals("", "   ".transformInInitials())
    }
}
