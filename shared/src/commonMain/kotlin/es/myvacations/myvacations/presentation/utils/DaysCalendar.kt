package es.myvacations.myvacations.presentation.utils

import androidx.compose.runtime.Composable
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.friday
import myvacations.shared.generated.resources.monday
import myvacations.shared.generated.resources.saturday
import myvacations.shared.generated.resources.sunday
import myvacations.shared.generated.resources.thursday
import myvacations.shared.generated.resources.tuesday
import myvacations.shared.generated.resources.wednesday
import org.jetbrains.compose.resources.stringResource

object DaysCalendar {
    @Composable
    fun daysCalendar(): List<String> {
        return listOf(
            stringResource(Res.string.monday),
            stringResource(Res.string.tuesday),
            stringResource(Res.string.wednesday),
            stringResource(Res.string.thursday),
            stringResource(Res.string.friday),
            stringResource(Res.string.saturday),
            stringResource(Res.string.sunday)
        )
    }
}