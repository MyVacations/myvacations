package es.myvacations.myvacations.presentation.events

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.domain.repository.CalendarAddEventResult
import es.myvacations.myvacations.domain.repository.DeviceCalendarRepository
import es.myvacations.myvacations.presentation.utils.calendar.CalendarStatus
import es.myvacations.myvacations.presentation.utils.calendar.DeviceCalendar
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.descriptioncalendar
import myvacations.shared.generated.resources.titlecalendar
import org.jetbrains.compose.resources.getString

actual class GetDeviceCalendarRepository actual constructor() :
    DeviceCalendarRepository {
    val context = AndroidContextHolder.context

    private var calendarObserver: ContentObserver? = null

    actual override fun hasCalendarPermission(): Boolean {
        try {
            val readPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            )

            val writePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_CALENDAR
            )
            return readPermission == PERMISSION_GRANTED &&
                    writePermission == PERMISSION_GRANTED
        } catch (e: Exception) {
            return false
        }
    }

    actual override fun observeCalendarChanges(
        onChange: () -> Unit
    ) {
        if (!hasCalendarPermission()) return
        if (calendarObserver != null) return

        calendarObserver = object : ContentObserver(
            Handler(Looper.getMainLooper())
        ) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                onChange()
            }
        }

        context.contentResolver.registerContentObserver(
            CalendarContract.Events.CONTENT_URI,
            true,
            calendarObserver!!
        )
    }

    actual override fun stopObservingCalendarChanges() {
        if (!hasCalendarPermission()) return
        calendarObserver?.let {
            context.contentResolver.unregisterContentObserver(it)
        }

        calendarObserver = null
    }

    private fun findAsyncWithCalendarId(tripId: String, id: String): Boolean {
        if (!hasCalendarPermission()) return false
        val selection =
            "${CalendarContract.Events.CUSTOM_APP_URI} = ? AND " +
                    "${CalendarContract.Events.CALENDAR_ID} = ?"

        val selectionArgs = arrayOf(
            "myvacations://trip/$tripId",
            id
        )

        context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            arrayOf(CalendarContract.Events._ID),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            return cursor.moveToFirst()
        }
        return false
    }

    private fun getCalendarColors(
        accountName: String,
        accountType: String
    ): List<CalendarColor> {
        val colors = mutableListOf<CalendarColor>()
        val projection = arrayOf(
            CalendarContract.Colors.COLOR_KEY,
            CalendarContract.Colors.COLOR
        )
        val selection =
            "${CalendarContract.Colors.ACCOUNT_NAME} = ? AND " +
                    "${CalendarContract.Colors.ACCOUNT_TYPE} = ? AND " +
                    "${CalendarContract.Colors.COLOR_TYPE} = ?"

        val selectionArgs = arrayOf(
            accountName,
            accountType,
            CalendarContract.Colors.TYPE_EVENT.toString()
        )
        context.contentResolver.query(
            CalendarContract.Colors.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            val keyIndex = cursor.getColumnIndexOrThrow(
                CalendarContract.Colors.COLOR_KEY
            )

            val colorIndex = cursor.getColumnIndexOrThrow(
                CalendarContract.Colors.COLOR
            )

            while (cursor.moveToNext()) {
                colors += CalendarColor(
                    key = cursor.getString(keyIndex),
                    color = cursor.getInt(colorIndex)
                )
            }
        }

        return colors
    }

    actual override fun getCalendars(id: String): List<DeviceCalendar> {
        if (!hasCalendarPermission()) {
            return emptyList()
        }

        val calendars = mutableListOf<DeviceCalendar>()
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE
        )

        val selection =
            "${CalendarContract.Calendars.VISIBLE} = 1 AND " +
                    "${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ?"

        val selectionArgs = arrayOf(
            CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString()
        )

        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(
                CalendarContract.Calendars._ID
            )

            val accountNameIndex = cursor.getColumnIndexOrThrow(
                CalendarContract.Calendars.ACCOUNT_NAME
            )

            val accountTypeIndex = cursor.getColumnIndexOrThrow(
                CalendarContract.Calendars.ACCOUNT_TYPE
            )

            while (cursor.moveToNext()) {

                calendars.add(
                    DeviceCalendar(
                        id = cursor.getLong(idIndex),
                        accountName = cursor.getString(accountNameIndex),
                        accountType = cursor.getString(accountTypeIndex),
                        isSync = findAsyncWithCalendarId(id, cursor.getLong(idIndex).toString()),
                        colorList = getCalendarColors(
                            cursor.getString(accountNameIndex),
                            cursor.getString(accountTypeIndex)
                        )
                    )
                )
            }
        }

        return calendars
    }

    actual override suspend fun eventExists(id: String): Boolean {
        if (!hasCalendarPermission()) return false
        val selection =
            "${CalendarContract.Events.CUSTOM_APP_URI} = ?"

        val selectionArgs = arrayOf(
            "myvacations://trip/$id"
        )

        context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            arrayOf(CalendarContract.Events._ID),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            return cursor.moveToFirst()
        }
        return false
    }

    actual override suspend fun addEditDeleteEvent(
        calendarId: String,
        tripId: String,
        title: String,
        place: String,
        startDate: LocalDate,
        endDate: LocalDate,
        color: CalendarColor,
        status: CalendarStatus
    ): CalendarAddEventResult {
        try {
            if (!hasCalendarPermission()) return CalendarAddEventResult.PermissionDenied

            val calendarId =
                if (status != CalendarStatus.INSERT) calendarId else calendarId.ifBlank { return CalendarAddEventResult.NoCalendarAvailable }
            val selection = if (status != CalendarStatus.INSERT && calendarId.isEmpty()) {
                "${CalendarContract.Events.CUSTOM_APP_URI} = ?"
            } else {
                "${CalendarContract.Events.CUSTOM_APP_URI} = ? AND " +
                        "${CalendarContract.Events.CALENDAR_ID} = ?"
            }

            val selectionArgs = if (status != CalendarStatus.INSERT && calendarId.isEmpty()) {
                arrayOf(
                    "myvacations://trip/$tripId"
                )
            } else {
                arrayOf(
                    "myvacations://trip/$tripId",
                    calendarId
                )
            }
            val values = ContentValues().apply {

                if (calendarId.isNotEmpty()) put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(
                    CalendarContract.Events.TITLE,
                    getString(Res.string.titlecalendar, place, title)
                )
                put(
                    CalendarContract.Events.DESCRIPTION,
                    getString(Res.string.descriptioncalendar, place)
                )
                put(CalendarContract.Events.EVENT_LOCATION, place)
                put(CalendarContract.Events.EVENT_COLOR_KEY, color.key)
                // Evento de día completo
                put(CalendarContract.Events.ALL_DAY, 1)

                put(
                    CalendarContract.Events.CUSTOM_APP_URI,
                    "myvacations://trip/$tripId"
                )

                put(
                    CalendarContract.Events.DTSTART,
                    startDate.toEpochDays() * 86_400_000L
                )

                put(
                    CalendarContract.Events.DTEND,
                    endDate.plus(1, DateTimeUnit.DAY).toEpochDays() * 86_400_000L
                )

                put(CalendarContract.Events.EVENT_TIMEZONE, "UTC")
            }

            when (status) {
                CalendarStatus.INSERT -> {
                    context.contentResolver.insert(
                        CalendarContract.Events.CONTENT_URI,
                        values
                    )
                }

                CalendarStatus.UPDATE -> {
                    context.contentResolver.update(
                        CalendarContract.Events.CONTENT_URI,
                        values,
                        selection,
                        selectionArgs
                    )
                }

                CalendarStatus.DELETE -> {
                    context.contentResolver.delete(
                        CalendarContract.Events.CONTENT_URI,
                        selection,
                        selectionArgs
                    )
                }
            }
            return CalendarAddEventResult.Success(status)
        } catch (e: Exception) {
            return CalendarAddEventResult.Error(e)
        }
    }
}

@Composable
actual fun CalendarPermissionHandler(
    onUpdatePermission: (CalendarAddEventResult) -> Unit,
    dialogRequestingCalendarPermissions: Boolean
) {
    if (dialogRequestingCalendarPermissions) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val readGranted =
                permissions[Manifest.permission.READ_CALENDAR] == true

            val writeGranted =
                permissions[Manifest.permission.WRITE_CALENDAR] == true

            onUpdatePermission(if (!readGranted || !writeGranted) CalendarAddEventResult.PermissionDenied else CalendarAddEventResult.NoCalendarAvailable)
        }

        LaunchedEffect(Unit) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
            )
        }
    }
}