package es.myvacations.myvacations.core.utils

import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format

object DateFormatter {

    fun formatTripDate(
        date: LocalDate,
    ): String {
        return if (Locale.current.language == "es") {
            date.format(spanishDateFormat)
        } else {
            date.format(englishDateFormat)
        }
    }

}