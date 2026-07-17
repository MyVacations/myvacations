package es.myvacations.myvacations.core.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlin.time.Clock

val MinDate = LocalDate(1900,1,1)
val MaxDate = LocalDate(2100,12,31)
fun LocalDateTime.toRelativeTime(
    nowString: String,
    minutesAgo: (Long) -> String,
    hoursAgo: (Long) -> String,
    yesterday: String,
    daysAgo: (Long) -> String
): String {
    val timeZone = TimeZone.currentSystemDefault()

    val now = Clock.System.now()
    val created = this.toInstant(timeZone)

    val duration = now - created

    val minutes = duration.inWholeMinutes
    val hours = duration.inWholeHours
    val days = duration.inWholeDays

    return when {
        minutes < 1 -> nowString
        minutes < 60 -> minutesAgo(minutes)
        hours < 24 -> hoursAgo(hours)
        days == 1L -> yesterday
        days < 7 -> daysAgo(days)
        else -> "${day.toString().padStart(2, '0')}/" +
                "${month.toString().padStart(2, '0')}/" +
                year
    }
}

fun String.formatDateInput(): String {
    val digits = this.filter { it.isDigit() }.take(8)

    return buildString {
        digits.forEachIndexed { index, c ->
            append(c)
            if ((index == 1 || index == 3) && index != digits.lastIndex) {
                append('/')
            }
        }
    }
}

fun String.validateDate(): Boolean {
    return try {
        // Intenta parsear la fecha con el formato del componente
        val parts = this.split("/")
        if (parts.size != 3) return false

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        if (year < MinDate.year) return false
        LocalDate(year, month, day)
        true
    } catch (e: Exception) {
        false
    }
}

fun kmpDateFormat() = LocalDate.Format {
    day()
    char('/')
    monthNumber()
    char('/')
    year()
}