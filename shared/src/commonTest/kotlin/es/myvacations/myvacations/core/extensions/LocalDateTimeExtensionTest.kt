package es.myvacations.myvacations.core.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocalDateTimeExtensionTest {

    data class ValidateDateTestCase(
        val input: String,
        val expected: Boolean,
        val description: String
    )

    @Test
    fun testValidateDate() {
        val testCases = listOf(
            // Valid dates
            ValidateDateTestCase("18/07/2026", true, "Normal valid date"),
            ValidateDateTestCase("01/01/1900", true, "MinDate boundary"),
            ValidateDateTestCase("31/12/2100", true, "MaxDate boundary"),
            ValidateDateTestCase("29/02/2024", true, "Leap year February 29"),
            
            // Invalid dates
            ValidateDateTestCase("31/04/2026", false, "April 31 is invalid"),
            ValidateDateTestCase("29/02/2025", false, "February 29 in non-leap year"),
            ValidateDateTestCase("31/12/1899", false, "Year before MinDate"),
            ValidateDateTestCase("18/07/202", false, "Incomplete/short year"),
            ValidateDateTestCase("18/07", false, "Missing year part"),
            ValidateDateTestCase("18", false, "Only day part"),
            ValidateDateTestCase("", false, "Empty string"),
            ValidateDateTestCase("abc/def/ghi", false, "Non-numeric input"),
            ValidateDateTestCase("18/07/202a", false, "Alphanumeric year")
        )

        for (case in testCases) {
            val actual = case.input.validateDate()
            assertEquals(
                case.expected,
                actual,
                "Failed validateDate case '${case.description}' with input '${case.input}'"
            )
        }
    }

    data class FormatDateTestCase(
        val input: String,
        val expected: String,
        val description: String
    )

    @Test
    fun testFormatDateInput() {
        val testCases = listOf(
            FormatDateTestCase("28022025", "28/02/2025", "Full digit sequence"),
            FormatDateTestCase("28022", "28/02/2", "Partial digit sequence (5 digits)"),
            FormatDateTestCase("280", "28/0", "Partial digit sequence (3 digits)"),
            FormatDateTestCase("28", "28", "Partial digit sequence (2 digits)"),
            FormatDateTestCase("28a02", "28/02", "Alphanumeric characters filtered out"),
            FormatDateTestCase("", "", "Empty input")
        )

        for (case in testCases) {
            val actual = case.input.formatDateInput()
            assertEquals(
                case.expected,
                actual,
                "Failed formatDateInput case '${case.description}' with input '${case.input}'"
            )
        }
    }

    @Test
    fun testToRelativeTime() {
        // We will test relative time boundaries with mock lambda callbacks
        val nowString = "now"
        val yesterday = "yesterday"
        val minutesAgo: (Long) -> String = { "$it mins ago" }
        val hoursAgo: (Long) -> String = { "$it hours ago" }
        val daysAgo: (Long) -> String = { "$it days ago" }

        // Note: LocalDateTime.toRelativeTime uses Clock.System.now(), which reads the system clock.
        // Therefore, we can construct LocalDateTime relative to the current local system time to make this deterministic.
        val zone = TimeZone.currentSystemDefault()
        val systemNow = Clock.System.now()
        
        // Helper to get LocalDateTime relative to now by adding/subtracting duration
        fun getRelativeDateTime(offset: kotlin.time.Duration): LocalDateTime {
            return (systemNow + offset).toLocalDateTime(zone)
        }

        // 1. Less than 1 minute ago -> nowString
        val nowDateTime = getRelativeDateTime(-kotlin.time.Duration.parse("30s"))
        assertEquals(nowString, nowDateTime.toRelativeTime(nowString, minutesAgo, hoursAgo, yesterday, daysAgo))

        // 2. Minutes ago (e.g. 5 minutes) -> minutesAgo
        val minutesAgoDateTime = getRelativeDateTime(-kotlin.time.Duration.parse("5m"))
        assertEquals("5 mins ago", minutesAgoDateTime.toRelativeTime(nowString, minutesAgo, hoursAgo, yesterday, daysAgo))

        // 3. Hours ago (e.g. 3 hours) -> hoursAgo
        val hoursAgoDateTime = getRelativeDateTime(-kotlin.time.Duration.parse("3h"))
        assertEquals("3 hours ago", hoursAgoDateTime.toRelativeTime(nowString, minutesAgo, hoursAgo, yesterday, daysAgo))

        // 4. Yesterday (e.g. 25 hours, which is 1 day and 1 hour) -> yesterday
        val yesterdayDateTime = getRelativeDateTime(-kotlin.time.Duration.parse("25h"))
        assertEquals(yesterday, yesterdayDateTime.toRelativeTime(nowString, minutesAgo, hoursAgo, yesterday, daysAgo))

        // 5. Days ago (e.g. 3 days) -> daysAgo
        val daysAgoDateTime = getRelativeDateTime(-kotlin.time.Duration.parse("73h")) // 3 days is 72 hours
        assertEquals("3 days ago", daysAgoDateTime.toRelativeTime(nowString, minutesAgo, hoursAgo, yesterday, daysAgo))

        // 6. Absolute date format for 7 days or more -> DD/MM/YYYY
        // We will create a specific date to ensure formatting matches
        val absoluteDateTime = LocalDateTime(2026, 7, 10, 12, 0)
        // Ensure it is far enough in the past (more than 7 days relative to systemNow)
        // If systemNow is 2026-07-18, then 2026-07-10 is 8 days ago.
        val durationDays = (systemNow - absoluteDateTime.toInstant(zone)).inWholeDays
        if (durationDays >= 7) {
            assertEquals("10/07/2026", absoluteDateTime.toRelativeTime(nowString, minutesAgo, hoursAgo, yesterday, daysAgo))
        }
    }
}
