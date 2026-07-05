package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import es.myvacations.myvacations.core.extensions.toMonthString
import es.myvacations.myvacations.presentation.utils.DaysCalendar.daysCalendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.accept
import myvacations.shared.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource


@Composable
fun MonthHeader(
    month: CalendarMonth
) {
    Column {

        Text(
            text = "${month.yearMonth.month.toMonthString().uppercase()} ${month.yearMonth.year}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            daysCalendar().forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}


@Composable
fun AppDatePickerDialog(
    onDismiss: () -> Unit = { },
    onDateSelected: (LocalDate) -> Unit = {}
) {
    var selectedDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    val currentMonth = remember {
        YearMonth.now()
    }

    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(48),
        endMonth = currentMonth.plusMonths(48),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate?.let(onDateSelected)
                    if(selectedDate != null) onDismiss()
                }
            ) {
                Text(stringResource(Res.string.accept))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(Res.string.cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {

                HorizontalCalendar(
                    modifier = Modifier.weight(1f),
                    state = state,

                    monthHeader = {
                        MonthHeader(it)
                    },

                    dayContent = { day ->
                        val isCurrentMonth =
                            day.position == DayPosition.MonthDate

                        val isSelected =
                            isCurrentMonth &&
                                    day.date == selectedDate

                        Box(
                            modifier = Modifier
                                .fillMaxWidth().aspectRatio(1.7f),
                            contentAlignment = Alignment.Center
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected)
                                            Color(0xFF8B5CF6)
                                        else
                                            Color.Transparent
                                    )
                                    .clickable(
                                        enabled = isCurrentMonth
                                    ) {
                                        selectedDate = day.date
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.date.day.toString(),
                                    color = when {
                                        isSelected ->
                                            Color.White

                                        isCurrentMonth ->
                                            MaterialTheme.colorScheme.onSurface

                                        else ->
                                            MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.25f
                                            )
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    )
}