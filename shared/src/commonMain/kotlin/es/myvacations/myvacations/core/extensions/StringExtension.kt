package es.myvacations.myvacations.core.extensions

import androidx.compose.runtime.Composable
import kotlinx.datetime.Month
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.april
import myvacations.shared.generated.resources.april_
import myvacations.shared.generated.resources.august
import myvacations.shared.generated.resources.august_
import myvacations.shared.generated.resources.december
import myvacations.shared.generated.resources.december_
import myvacations.shared.generated.resources.february
import myvacations.shared.generated.resources.february_
import myvacations.shared.generated.resources.january
import myvacations.shared.generated.resources.january_
import myvacations.shared.generated.resources.july
import myvacations.shared.generated.resources.july_
import myvacations.shared.generated.resources.june
import myvacations.shared.generated.resources.june_
import myvacations.shared.generated.resources.march
import myvacations.shared.generated.resources.march_
import myvacations.shared.generated.resources.may
import myvacations.shared.generated.resources.may_
import myvacations.shared.generated.resources.november
import myvacations.shared.generated.resources.november_
import myvacations.shared.generated.resources.october
import myvacations.shared.generated.resources.october_
import myvacations.shared.generated.resources.september
import myvacations.shared.generated.resources.september_
import org.jetbrains.compose.resources.stringResource

fun String.shortenTitle(maxLength: Int = 8) =
    if (this.length > maxLength) this.take(maxLength - 2) + "..." else this

fun String.toSafeDouble(): Double {
    return this
        .replace(',', '.')
        .takeIf { it.isNotBlank() }
        ?.toDoubleOrNull()
        ?: 0.0
}

@Composable
fun Month.toMonthString() = when (this) {
    Month.JANUARY -> stringResource(Res.string.january)
    Month.FEBRUARY -> stringResource(Res.string.february)
    Month.MARCH -> stringResource(Res.string.march)
    Month.APRIL -> stringResource(Res.string.april)
    Month.MAY -> stringResource(Res.string.may)
    Month.JUNE -> stringResource(Res.string.june)
    Month.JULY -> stringResource(Res.string.july)
    Month.AUGUST -> stringResource(Res.string.august)
    Month.SEPTEMBER -> stringResource(Res.string.september)
    Month.OCTOBER -> stringResource(Res.string.october)
    Month.NOVEMBER -> stringResource(Res.string.november)
    Month.DECEMBER -> stringResource(Res.string.december)
}

@Composable
fun Month.toMonth_String() = when (this) {
    Month.JANUARY -> stringResource(Res.string.january_)
    Month.FEBRUARY -> stringResource(Res.string.february_)
    Month.MARCH -> stringResource(Res.string.march_)
    Month.APRIL -> stringResource(Res.string.april_)
    Month.MAY -> stringResource(Res.string.may_)
    Month.JUNE -> stringResource(Res.string.june_)
    Month.JULY -> stringResource(Res.string.july_)
    Month.AUGUST -> stringResource(Res.string.august_)
    Month.SEPTEMBER -> stringResource(Res.string.september_)
    Month.OCTOBER -> stringResource(Res.string.october_)
    Month.NOVEMBER -> stringResource(Res.string.november_)
    Month.DECEMBER -> stringResource(Res.string.december_)
}