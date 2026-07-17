package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import es.myvacations.myvacations.core.extensions.MaxDate
import es.myvacations.myvacations.core.extensions.MinDate
import es.myvacations.myvacations.core.extensions.formatDateInput
import es.myvacations.myvacations.core.extensions.kmpDateFormat
import es.myvacations.myvacations.core.extensions.toMonthString
import es.myvacations.myvacations.core.extensions.toMonth_String
import es.myvacations.myvacations.core.extensions.validateDate
import es.myvacations.myvacations.presentation.utils.DaysCalendar.daysCalendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format
import kotlinx.datetime.yearMonth
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.accept
import myvacations.shared.generated.resources.cancel
import myvacations.shared.generated.resources.new_trip_select_date
import myvacations.shared.generated.resources.today
import org.jetbrains.compose.resources.stringResource

@Composable
fun HeaderCalendarUi(
    selectedDate: LocalDate,
    updateDate: (LocalDate) -> Unit = {},
    showTextDateEditor: Boolean,
    showYearPicker: Boolean,
    month: CalendarMonth,
    updateShowTextDateEditor: (Boolean) -> Unit,
    updateShowYearPicker: (Boolean) -> Unit = {},
    updateDateValid: (Boolean) -> Unit,
) {
    var dateText by remember { mutableStateOf(selectedDate.format(kmpDateFormat())) }

    LaunchedEffect(selectedDate) {
        dateText = selectedDate.format(kmpDateFormat())
    }

    var calendarPosition by remember { mutableStateOf(selectedDate) }
    Column {
        if (showTextDateEditor) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "${selectedDate.day} ${selectedDate.month.toMonth_String()} ${selectedDate.year}"
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = {
                        updateShowTextDateEditor(false)
                    }
                )
                {
                    Icon(Icons.Default.DateRange, "dateRange")
                }
            }
            Spacer(Modifier.height(16.dp))
            BasicTextField(
                value = dateText,
                onValueChange = { value ->
                    val formatted = value.formatDateInput()
                    dateText = formatted
                    if (dateText.validateDate()) {
                        val date = kmpDateFormat().parse(dateText)
                        updateDateValid(true)
                        updateDate(date)
                    } else {
                        updateDateValid(false)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(Color.White),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White, shape = RoundedCornerShape(6.dp))
                            .padding(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            ), contentAlignment = CenterStart
                    )
                    {
                        innerTextField()
                    }
                }
            )

        } else {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "${selectedDate.day} ${selectedDate.month.toMonth_String()} ${selectedDate.year}"
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { updateShowTextDateEditor(true) }
                )
                {
                    Icon(Icons.Default.Edit, "edit")
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Row(Modifier.clickable(onClick = {
                    updateShowYearPicker(true)
                })) {
                    Text("${month.yearMonth.month.toMonthString()} - ${month.yearMonth.year}")
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowDropDown, "dropdown")
                }

                Spacer(Modifier.weight(1f))
                if (!showYearPicker) {
                    Row {
                        IconButton(
                            onClick = {
                                calendarPosition = calendarPosition.plusMonths(-1)
                                updateDate(calendarPosition)
                            }
                        )
                        {
                            Icon(Icons.AutoMirrored.Filled.ArrowLeft, "left")
                        }
                        Spacer(Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                calendarPosition = calendarPosition.plusMonths(1)
                                updateDate(calendarPosition)
                            }
                        )
                        {
                            Icon(Icons.AutoMirrored.Filled.ArrowRight, "right")
                        }
                    }
                }
            }
            DaysInCalendarHeader(showYearPicker)
        }
        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
fun DaysInCalendarHeader(showYearPicker: Boolean) {
    if (!showYearPicker) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            daysCalendar().forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun AppDatePickerDialog(
    fromParentUi: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit = { },
    onDateSelected: (LocalDate) -> Unit = {}
) {

    var isDateValid by remember { mutableStateOf(true) }

    var selectedDate by remember {
        mutableStateOf(fromParentUi)
    }

    var updateYearMonth by remember {
        mutableStateOf(selectedDate.yearMonth)
    }
    val state = rememberCalendarState(
        startMonth = MinDate.yearMonth,
        endMonth = MaxDate.yearMonth,
        firstVisibleMonth = updateYearMonth,
        firstDayOfWeek = firstDayOfWeekFromLocale(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

    LaunchedEffect(updateYearMonth) {
        state.animateScrollToMonth(updateYearMonth)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isDateValid) {
                        onDateSelected(selectedDate)
                        onDismiss()
                    }
                }
            ) {
                Text(
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = if (isDateValid) 1f else 0.5f
                    ), text = stringResource(Res.string.accept)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    selectedDate = LocalDate.now()
                    updateYearMonth = selectedDate.yearMonth
                }
            ) {
                Text(stringResource(Res.string.today))
            }
            Spacer(Modifier.width(18.dp))
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(Res.string.cancel))
            }
        },
        text = {
            InsideTextAlert(
                state, updateYearMonth, selectedDate, updateDate = {
                    selectedDate = it
                    updateYearMonth = it.yearMonth
                }, updateDateValid = { isDateValid = it },
                updateCalendarYear = {
                    updateYearMonth = it.yearMonth
                })
        }
    )
}

@Composable
private fun InsideTextAlert(
    state: CalendarState,
    selectedInCalendar: YearMonth,
    selectedDate: LocalDate,
    updateDate: (LocalDate) -> Unit,
    updateDateValid: (Boolean) -> Unit,
    updateCalendarYear: (LocalDate) -> Unit
) {
    var showTextDateEditor by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }
    var selectedDateInside by remember {
        mutableStateOf(selectedDate)
    }
    val listState = rememberLazyListState()
    val currentRow = (selectedDate.year - MinDate.year) / 3
    LaunchedEffect(showYearPicker) {
        if (showYearPicker) {
            listState.scrollToItem(currentRow)
        }
    }

    BoxWithConstraints {
        val maxCalendarHeight = maxHeight * 0.75f
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxCalendarHeight)
        ) {
            Text(stringResource(Res.string.new_trip_select_date))
            Spacer(Modifier.height(8.dp))

            HeaderCalendarUi(
                selectedDate = selectedDateInside,
                updateDate = updateDate,
                showTextDateEditor = showTextDateEditor,
                showYearPicker = showYearPicker,
                month = state.firstVisibleMonth,
                updateShowTextDateEditor = { showTextDate ->
                    showTextDateEditor = showTextDate
                    showYearPicker = false
                },
                updateShowYearPicker = { showYear ->
                    showYearPicker = showYear
                },
                updateDateValid = updateDateValid
            )
            Spacer(Modifier.height(8.dp))
            HorizontalCalendar(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                userScrollEnabled = !showYearPicker,
                monthHeader = {
                    YearScreen(
                        showTextDateEditor,
                        showYearPicker,
                        selectedInCalendar,
                        updateCalendarYear,
                        listState,
                        selectedDateInside,
                        updateYearPicker = { showYearPicker = it })
                },
                dayContent = { day ->
                    DayScreen(
                        day,
                        showTextDateEditor,
                        showYearPicker,
                        selectedDateInside,
                        updateDate,
                        updateSelectedDate = { selectedDateInside = it })

                }
            )
        }
    }
}

@Composable
fun DayScreen(
    day: CalendarDay,
    showTextDateEditor: Boolean,
    showYearPicker: Boolean,
    selectedDateInside: LocalDate,
    updateDate: (LocalDate) -> Unit,
    updateSelectedDate: (LocalDate) -> Unit
) {
    if (!showTextDateEditor && !showYearPicker) {
        val isCurrentMonth =
            day.position == DayPosition.MonthDate

        val isSelected =
            isCurrentMonth &&
                    day.date == selectedDateInside

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
                        updateSelectedDate(day.date)
                        updateDate(day.date)
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
}

@Composable
fun YearScreen(
    showTextDateEditor: Boolean,
    showYearPicker: Boolean,
    selectedInCalendar: YearMonth,
    updateCalendarYear: (LocalDate) -> Unit,
    listState: LazyListState,
    selectedDateInside: LocalDate,
    updateYearPicker: (Boolean) -> Unit = {}
) {
    if (!showTextDateEditor && showYearPicker) {
        LazyColumn(state = listState) {
            items((MinDate.year..MaxDate.year).chunked(3)) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { year ->
                        val isSelected = selectedInCalendar.year == year
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected)
                                        Color(0xFF8B5CF6)
                                    else
                                        Color.Transparent
                                ).clickable {
                                    updateCalendarYear(
                                        LocalDate(
                                            year,
                                            selectedDateInside.month,
                                            selectedDateInside.day
                                        )
                                    )
                                    updateYearPicker(false)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = year.toString(),
                                color = when {
                                    isSelected ->
                                        Color.White

                                    else ->
                                        MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

