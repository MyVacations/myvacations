package es.myvacations.myvacations.presentation.utils.calendar

import androidx.compose.runtime.Composable
import es.myvacations.myvacations.presentation.events.CalendarColor
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.calendar_dav
import myvacations.shared.generated.resources.calendar_exchange
import myvacations.shared.generated.resources.calendar_google
import myvacations.shared.generated.resources.calendar_local
import myvacations.shared.generated.resources.calendar_microsoft
import org.jetbrains.compose.resources.stringResource

data class DeviceCalendar(
    val id: Long,
    val accountName: String,
    val accountType: String,
    val isSync: Boolean,
    val colorList: List<CalendarColor>
)

enum class CalendarStatus{
    INSERT,
    UPDATE,
    DELETE
}

@Composable
fun String.loadNameFromAccountType() = when (this) {
    "com.google" -> stringResource(Res.string.calendar_google)
    "com.android.exchange" -> stringResource(Res.string.calendar_exchange)
    "com.microsoft.exchange","com.microsoft.office.outlook" -> stringResource(Res.string.calendar_microsoft)
    "org.dmfs.caldav.account" -> stringResource(Res.string.calendar_dav)
    else -> stringResource(Res.string.calendar_local)
}