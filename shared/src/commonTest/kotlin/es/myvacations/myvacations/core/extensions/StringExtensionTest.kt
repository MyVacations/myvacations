package es.myvacations.myvacations.core.extensions

import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtensionTest {

    @Test
    fun shortenTitle_shortensTitleCorrectly() {
        assertEquals("Hello...", "HelloWorld".shortenTitle(7))
        assertEquals("Trip ...", "Trip to Paris".shortenTitle(7))
        assertEquals("Trip to Paris", "Trip to Paris".shortenTitle(20))
        assertEquals("Trip", "Trip".shortenTitle(4))
    }

    @Test
    fun toSafeDouble_parsesStringsCorrectly() {
        assertEquals(10.5, "10.5".toSafeDouble())
        assertEquals(10.5, "10,5".toSafeDouble())
        assertEquals(0.0, "".toSafeDouble())
        assertEquals(0.0, "   ".toSafeDouble())
        assertEquals(0.0, "invalid".toSafeDouble())
    }
}
