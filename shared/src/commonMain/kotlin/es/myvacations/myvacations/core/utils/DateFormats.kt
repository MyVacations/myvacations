package es.myvacations.myvacations.core.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames

val englishDateFormat = LocalDate.Format {
    monthName(
        MonthNames.ENGLISH_ABBREVIATED
    )
    chars(" ")
    day()
    chars(", ")
    year()
}

val spanishDateFormat = LocalDate.Format {
    day()
    chars(" ")
    monthName(
        MonthNames(
            listOf(
                "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "ago", "Sep", "Oct", "Nov", "Dic"
            )
        )
    )
    chars(" ")
    year()
}