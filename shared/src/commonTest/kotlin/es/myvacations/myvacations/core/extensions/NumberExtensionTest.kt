package es.myvacations.myvacations.core.extensions

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberExtensionTest {

    @Test
    fun shortCurrency_formatsCorrectly() {
        // Less than 1000
        assertEquals("999", 999.0.shortCurrency())
        assertEquals("0", 0.0.shortCurrency())

        // Between 1000 and 10000
        assertEquals("1.0K", 1000.0.shortCurrency())
        assertEquals("1.5K", 1500.0.shortCurrency())
        assertEquals("9.9K", 9940.0.shortCurrency())

        // Between 10000 and 100000
        assertEquals("10.0K", 10000.0.shortCurrency())
        assertEquals("50.0K", 50000.0.shortCurrency())

        // Between 100000 and 1000000
        assertEquals("100.0K", 100000.0.shortCurrency())
        assertEquals("250.0K", 250000.0.shortCurrency())

        // Millions
        assertEquals("1.0M", 1000000.0.shortCurrency())
        assertEquals("1.5M", 1500000.0.shortCurrency())
        assertEquals("10.0M", 10000000.0.shortCurrency())
    }
}
