package es.myvacations.myvacations.core.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Clock


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