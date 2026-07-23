package es.myvacations.myvacations.presentation.createedittrip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.myvacations.myvacations.core.extensions.toSafeDouble
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.displayName
import es.myvacations.myvacations.domain.model.flag
import es.myvacations.myvacations.presentation.utils.AppDatePickerDialog
import es.myvacations.myvacations.presentation.utils.AppDropDown
import es.myvacations.myvacations.presentation.utils.ExpenseItem
import es.myvacations.myvacations.presentation.utils.SummaryCard
import es.myvacations.myvacations.presentation.utils.TravelIcon
import es.myvacations.myvacations.presentation.utils.TripExpenseUiState
import es.myvacations.myvacations.presentation.utils.painter
import es.myvacations.myvacations.presentation.utils.toCurrencySymbol
import es.myvacations.myvacations.presentation.utils.toImageVector
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.accept
import myvacations.shared.generated.resources.cancel
import myvacations.shared.generated.resources.edt_trip
import myvacations.shared.generated.resources.error
import myvacations.shared.generated.resources.new_trip_add_expense
import myvacations.shared.generated.resources.new_trip_budget
import myvacations.shared.generated.resources.new_trip_clear
import myvacations.shared.generated.resources.new_trip_clear_formulary
import myvacations.shared.generated.resources.new_trip_complete_formulary
import myvacations.shared.generated.resources.new_trip_cost
import myvacations.shared.generated.resources.new_trip_cost_budget
import myvacations.shared.generated.resources.new_trip_cost_require
import myvacations.shared.generated.resources.new_trip_cover_dialog_title
import myvacations.shared.generated.resources.new_trip_dates
import myvacations.shared.generated.resources.new_trip_days
import myvacations.shared.generated.resources.new_trip_destination
import myvacations.shared.generated.resources.new_trip_duration
import myvacations.shared.generated.resources.new_trip_end_date
import myvacations.shared.generated.resources.new_trip_error
import myvacations.shared.generated.resources.new_trip_expense
import myvacations.shared.generated.resources.new_trip_food
import myvacations.shared.generated.resources.new_trip_optional_amount
import myvacations.shared.generated.resources.new_trip_optional_delete
import myvacations.shared.generated.resources.new_trip_optional_expenses
import myvacations.shared.generated.resources.new_trip_optional_name
import myvacations.shared.generated.resources.new_trip_save_trip
import myvacations.shared.generated.resources.new_trip_start_date
import myvacations.shared.generated.resources.new_trip_title
import myvacations.shared.generated.resources.new_trip_title_require
import myvacations.shared.generated.resources.new_trip_trip_country
import myvacations.shared.generated.resources.new_trip_trip_title
import myvacations.shared.generated.resources.new_trip_trip_title_example
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

const val maxTextLength = 30

@Composable
fun AddEditTripScreen(
    tripId: String,
    onDismiss: () -> Unit,
    viewModel: CreateEditTripsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SystemBackHandler {
        viewModel.clearUi()
    }
    LaunchedEffect(tripId) {
        if (tripId.isEmpty()) return@LaunchedEffect

        viewModel.setLoading(true)

        viewModel.updateEditMode(tripId)
        viewModel.getTripById(tripId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
    } else {
        AddTripScreenFormulary(
            onDismiss,
            uiState,
            onTitleTripChange = viewModel::updateTripTitle,
            onCountrySelected = viewModel::updateCountry,
            onCoverSelected = viewModel::updateCover,
            onStartDateChange = viewModel::updateStartDate,
            onEndDateChange = viewModel::updateEndDate,
            onTravelersChange = viewModel::updateTravelers,
            onMainCostChange = viewModel::updateMainCost,
            onMainBudgetChange = viewModel::updateMainBudget,
            toggleOptionalExpenses = viewModel::toggleOptionalExpenses,
            onUpdateErrorAmount = viewModel::updateErrorAmount,
            onCreateExpense = viewModel::createExpense,
            onUpdateExpense = viewModel::updateExpense,
            onDeleteExpense = viewModel::deleteExpense,
            onSave = viewModel::saveTrip,
            clearUI = viewModel::clearUi,
            updateFavourite = viewModel::updateFavourite
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AddTripScreenFormulary(
    onDismiss: () -> Unit = {},
    uiState: TripUiState = TripUiState(),
    onTitleTripChange: (String) -> Unit = {},
    onCountrySelected: (Country) -> Unit = {},
    onCoverSelected: (TripCover) -> Unit = {},
    onStartDateChange: (LocalDate) -> Unit = {},
    onEndDateChange: (LocalDate) -> Unit = {},
    onDaysTravelingChange: (Int) -> Unit = {},
    onTravelersChange: (Int) -> Unit = {},
    onMainCostChange: (String) -> Unit = {},
    onMainBudgetChange: (String) -> Unit = {},
    toggleOptionalExpenses: () -> Unit = {},
    onCreateExpense: (String, String, String, TravelIcon) -> Unit = { _, _, _, _ -> },
    onUpdateExpense: (String, String, String, TravelIcon) -> Unit = { _, _, _, _ -> },
    onUpdateErrorAmount: (Boolean) -> Unit = {},
    onDeleteExpense: (String) -> Unit = {},
    onSave: () -> Unit = {},
    clearUI: () -> Unit = {},
    updateFavourite: (Boolean) -> Unit = {}
) {
    SystemBackHandler {
        onDismiss()
    }
    val dialogClear = remember { mutableStateOf(false) }
    val dialogSaveNotReady = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize().padding(top = 12.dp)
    ) { _ ->
        if (dialogClear.value) {
            AlertDialog(
                onDismissRequest = { dialogClear.value = false },
                title = { Text(stringResource(Res.string.new_trip_clear_formulary)) },
                confirmButton = {
                    Text(
                        modifier = Modifier.clickable(onClick = {
                            clearUI()
                            dialogClear.value = false
                        }),
                        text = stringResource(Res.string.accept),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                dismissButton = {
                    Text(modifier = Modifier.clickable(onClick = {
                        dialogClear.value = false
                    }), text = stringResource(Res.string.cancel))
                }
            )
        }
        if (dialogSaveNotReady.value) {
            AlertDialog(
                onDismissRequest = { dialogClear.value = false },
                title = { Text(stringResource(Res.string.new_trip_error)) },
                text = { Text(stringResource(Res.string.new_trip_complete_formulary)) },
                confirmButton = {
                    Text(
                        modifier = Modifier.clickable(onClick = {
                            dialogSaveNotReady.value = false
                        }),
                        text = stringResource(Res.string.accept)
                    )
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    clearUI()
                    onDismiss()
                }) {
                    Icon(Icons.Default.Close, null)
                }

                Text(
                    text = stringResource(if (uiState.editMode) Res.string.edt_trip else Res.string.new_trip_title),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { dialogClear.value = true }) {
                    Text(
                        text = stringResource(Res.string.new_trip_clear),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    if (!uiState.errorInScreen && (uiState.titleTrip.isEmpty() || uiState.startDate == null || uiState.endDate == null || (uiState.mainCost == 0.0 || uiState.mainCost == null) || (uiState.startDate > uiState.endDate)).not()) {
                        onSave()
                        onDismiss()
                    } else {
                        dialogSaveNotReady.value = true
                    }
                }) {
                    Text(stringResource(Res.string.new_trip_save_trip))
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    DestinationView(
                        uiState,
                        onTitleTripChange,
                        onCountrySelected,
                        onCoverSelected,
                        updateFavourite = updateFavourite
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    DatesView(
                        uiState,
                        onStartDateChange,
                        onEndDateChange
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    DurationAndGroupView(uiState, onDaysTravelingChange, onTravelersChange)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    CostAndBudgetView(uiState, onMainCostChange, onMainBudgetChange)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    ExtraExpensesView(
                        uiState,
                        onToggleExpanded = toggleOptionalExpenses,
                        onUpdateErrorAmount = onUpdateErrorAmount,
                        onDeleteExpense = onDeleteExpense,
                        onCreateExpense = onCreateExpense,
                        onUpdateExpense = onUpdateExpense,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    if (uiState.errorInScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.error),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else TotalEstimatedCostView(uiState)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DestinationView(
    uiState: TripUiState = TripUiState(),
    onTitleTripChange: (String) -> Unit = {},
    onCountrySelected: (Country) -> Unit = {},
    onCoverSelected: (TripCover) -> Unit = {},
    updateFavourite: (Boolean) -> Unit = {}
) {
    var showCoverDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(Res.string.new_trip_destination),
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    updateFavourite(!uiState.favourite)
                },
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (uiState.favourite) Color.Yellow else Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.new_trip_trip_title),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = uiState.titleTrip,
            onValueChange = { newValue ->
                val filtered = newValue.filter {
                    it.isLetter() || it.isWhitespace()
                }

                if (filtered.length <= maxTextLength) {
                    onTitleTripChange(filtered)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF1E1E1E))
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        )
                ) {
                    if (uiState.titleTrip.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.new_trip_trip_title_example),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    innerTextField()
                }
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            if (uiState.titleTrip.isEmpty()) {
                Text(
                    text = stringResource(Res.string.new_trip_title_require),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${uiState.titleTrip.length}/30",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.new_trip_trip_country),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppDropDown(
            items = Country.entries,
            selectedItem = uiState.placeTrip,
            onItemSelected = onCountrySelected,
            itemLabel = { it.flag + " " + it.displayName() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable {
                    showCoverDialog = true
                }
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = uiState.cover.painter(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        if (showCoverDialog) {
            AlertDialog(
                onDismissRequest = { showCoverDialog = false },
                title = { Text(stringResource(Res.string.new_trip_cover_dialog_title)) },
                text = {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2)
                    ) {
                        items(TripCover.entries) { cover ->
                            Image(
                                painter = cover.painter(),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        onCoverSelected(cover)
                                        showCoverDialog = false
                                    }
                            )
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DatesView(
    uiState: TripUiState = TripUiState(),
    onStartDateChange: (LocalDate) -> Unit = {},
    onEndDateChange: (LocalDate) -> Unit = {}
) {
    val showDatePickerStart = remember { mutableStateOf(false) }
    val showDatePickerEnd = remember { mutableStateOf(false) }
    Text(
        text = stringResource(Res.string.new_trip_dates),
        style = MaterialTheme.typography.labelMedium,
        letterSpacing = 2.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.new_trip_start_date),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1E1E))
                    .clickable(onClick = { showDatePickerStart.value = true })
                    .padding(
                        horizontal = 16.dp,
                        vertical = 18.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.startDate.let {
                            it.let { date ->
                                "${
                                    date.day.toString().padStart(2, '0')
                                }/${date.month.number.toString().padStart(2, '0')}/${date.year}"
                            }
                        },
                        color = if (uiState.errorStartDate) MaterialTheme.colorScheme.error else Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = if (uiState.errorStartDate) Icons.Default.Error else Icons.Default.DateRange,
                        tint = if (uiState.errorStartDate) MaterialTheme.colorScheme.error else LocalContentColor.current,
                        contentDescription = null
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.new_trip_end_date),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1E1E))
                    .clickable(onClick = { showDatePickerEnd.value = true })
                    .padding(
                        horizontal = 16.dp,
                        vertical = 18.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.endDate.let {
                            it.let { date ->
                                "${
                                    date.day.toString().padStart(2, '0')
                                }/${date.month.number.toString().padStart(2, '0')}/${date.year}"
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            }
        }
    }
    ScreenPickerDialog(
        uiState,
        showDatePickerStart,
        showDatePickerEnd,
        onEndDateChange,
        onStartDateChange
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ScreenPickerDialog(
    uiState: TripUiState,
    showDatePickerStart: MutableState<Boolean>,
    showDatePickerEnd: MutableState<Boolean>,
    onEndDateChange: (LocalDate) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
) {
    if (showDatePickerEnd.value) AppDatePickerDialog(
        uiState.endDate,
        onDismiss = { showDatePickerEnd.value = false },
        onDateSelected = onEndDateChange
    )
    if (showDatePickerStart.value) AppDatePickerDialog(
        uiState.startDate,
        onDismiss = { showDatePickerStart.value = false },
        onDateSelected = onStartDateChange
    )
}

@Preview(showBackground = true)
@Composable
fun DurationAndGroupView(
    uiState: TripUiState = TripUiState(),
    onDaysTravelingChange: (Int) -> Unit = {},
    onTravelersChange: (Int) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.new_trip_duration) + ": ",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if (uiState.totalDays <= 0) "-" else uiState.totalDays.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(Res.string.new_trip_days),
            style = MaterialTheme.typography.bodySmall
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview(showBackground = true)
@Composable
fun CostAndBudgetView(
    uiState: TripUiState = TripUiState(),
    onMainCostChange: (String) -> Unit = {},
    onMainBudgetChange: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.new_trip_cost_budget),
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.new_trip_cost),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))
        var textCost by rememberSaveable {
            mutableStateOf(
                if (uiState.mainCost == 0.0) "" else uiState.mainCost.toString()
            )
        }
        BasicTextField(
            value = textCost,
            onValueChange = { newValue ->
                textCost = newValue
                onMainCostChange(newValue)
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                DecorationBoxCostMain(uiState.mainCost) {
                    innerTextField()
                }
            }
        )
        if (uiState.mainCost == 0.0) {
            Text(
                text = stringResource(Res.string.new_trip_cost_require),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.new_trip_budget, uiState.currency.toCurrencySymbol()),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        var textBudget by rememberSaveable {
            mutableStateOf(
                if (uiState.mainBudget == 0.0) "" else uiState.mainBudget.toString()
            )
        }
        BasicTextField(
            value = textBudget,
            onValueChange = { newValue ->
                textBudget = newValue
                onMainBudgetChange(newValue)
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
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF1E1E1E))
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        )
                ) {
                    if (uiState.mainBudget.toString() == "0.0" || uiState.mainBudget.toString()
                            .isEmpty()
                    ) {
                        Text(
                            text = "0.0",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    innerTextField()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExtraExpensesView(
    uiState: TripUiState = TripUiState(),
    onToggleExpanded: () -> Unit = {},
    onDeleteExpense: (String) -> Unit = {},
    onCreateExpense: (String, String, String, TravelIcon) -> Unit = { _, _, _, _ -> },
    onUpdateExpense: (String, String, String, TravelIcon) -> Unit = { _, _, _, _ -> },
    onUpdateErrorAmount: (Boolean) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onToggleExpanded
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.new_trip_optional_expenses),
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector =
                    if (uiState.optionalExpensesExpanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        val openEditDialog = remember { mutableStateOf(false) }
        val openAddingNew = remember { mutableStateOf(false) }

        if (uiState.optionalExpenses.isEmpty()) {
            AlertDialogExpense(
                uiState = uiState,
                onUpdateErrorAmount = onUpdateErrorAmount,
                onCreateExpense = onCreateExpense,
                onUpdateExpense = onUpdateExpense,
                openEditDialog = openEditDialog,
                onDelete = onDeleteExpense,
                addingNewOne = openAddingNew
            )
        }

        if (uiState.optionalExpensesExpanded) {
            uiState.optionalExpenses.forEach { expense ->
                ExpenseItem(
                    expense,
                    onDelete = { onDeleteExpense(expense.id) },
                    openEditDialog
                )

                AlertDialogExpense(
                    uiState = uiState,
                    editExpense = expense,
                    onUpdateErrorAmount = onUpdateErrorAmount,
                    onCreateExpense = onCreateExpense,
                    onUpdateExpense = onUpdateExpense,
                    openEditDialog = openEditDialog,
                    onDelete = onDeleteExpense,
                    addingNewOne = openAddingNew
                )
            }
            Spacer(
                modifier = Modifier.height(12.dp)
            )
            TextButton(
                onClick =
                    {
                        openAddingNew.value = true
                        openEditDialog.value = true
                    }
            ) {
                Text(stringResource(Res.string.new_trip_add_expense))
            }
        }
    }
}

@Composable
fun AlertDialogExpense(
    uiState: TripUiState = TripUiState(),
    onUpdateErrorAmount: (Boolean) -> Unit = {},
    onCreateExpense: (String, String, String, TravelIcon) -> Unit = { _, _, _, _ -> },
    onUpdateExpense: (String, String, String, TravelIcon) -> Unit = { _, _, _, _ -> },
    openEditDialog: MutableState<Boolean> = mutableStateOf(false),
    editExpense: TripExpenseUiState = TripExpenseUiState(),
    onDelete: (String) -> Unit = {},
    addingNewOne: MutableState<Boolean> = mutableStateOf(false)
) {
    val onExpenseName = remember { mutableStateOf(editExpense.name) }
    var textCost by rememberSaveable {
        mutableStateOf(
            if (editExpense.amount == 0.0) "" else editExpense.amount.toString()
        )
    }
    val onExpenseIcon = remember { mutableStateOf(editExpense.icon) }
    if (openEditDialog.value) {
        AlertDialog(
            onDismissRequest = {
                addingNewOne.value = false
                openEditDialog.value = false
            }, title = {
                Text(stringResource(Res.string.new_trip_expense))
            },

            text = {
                ScreenElementsAlert(
                    uiState, onExpenseName, editExpense, textCost,
                    onExpenseIcon,
                    updateName = {
                        onExpenseName.value = it
                    },
                    updateCost = {
                        textCost = it
                        if (textCost.toSafeDouble() > 999.9) onUpdateErrorAmount(true) else onUpdateErrorAmount(
                            false
                        )
                    },
                    updateIcon = {
                        onExpenseIcon.value = it
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClickFromConfirm(
                            onExpenseName,
                            textCost,
                            uiState,
                            addingNewOne,
                            onCreateExpense,
                            onExpenseIcon,
                            onUpdateExpense,
                            editExpense,
                            updateDialog = {
                                openEditDialog.value = false
                                addingNewOne.value = false
                            })
                    }) {
                    Text(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = if ((onExpenseName.value.isBlank() || (textCost == "0.0" || textCost == "0" || textCost == "0." || textCost.isEmpty()) || uiState.optionalExpensesErrorAmount).not()) 1f else 0.5f),
                        text = stringResource(Res.string.new_trip_save_trip)
                    )
                }
            },

            dismissButton = {
                Row {
                    if (!addingNewOne.value) {
                        TextButton(
                            onClick = { onDelete(editExpense.id) }
                        ) {
                            Text(
                                stringResource(Res.string.new_trip_optional_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            openEditDialog.value = false
                            addingNewOne.value = false
                        }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            })
    }
}

@Composable
fun ScreenElementsAlert(
    uiState: TripUiState,
    onExpenseName: MutableState<String>,
    editExpense: TripExpenseUiState,
    textCost: String,
    onExpenseIcon: MutableState<TravelIcon>,
    updateName: (String) -> Unit,
    updateCost: (String) -> Unit,
    updateIcon: (TravelIcon) -> Unit
) {
    Column {
        Text(stringResource(Res.string.new_trip_optional_name))
        Spacer(
            modifier = Modifier.height(8.dp)
        )

        BasicTextField(
            value = onExpenseName.value,
            onValueChange = { newValue ->
                val filtered = newValue.filter {
                    it.isLetter() || it.isWhitespace()
                }

                if (filtered.length <= maxTextLength) {
                    updateName(filtered)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF1E1E1E))
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        )
                ) {
                    if (onExpenseName.value.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.new_trip_food),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    innerTextField()
                }
            }
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            stringResource(
                Res.string.new_trip_optional_amount,
                editExpense.currency.toCurrencySymbol()
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        BasicTextField(
            value = textCost,
            onValueChange = { newValue ->
                updateCost(newValue)
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (uiState.optionalExpensesErrorAmount) MaterialTheme.colorScheme.error else Color.White,
            ),
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                DecorationBoxCost(textCost) {
                    innerTextField()
                }
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4), modifier = Modifier.height(200.dp)
        ) {
            items(
                TravelIcon.entries.size
            ) { index ->
                val icon = TravelIcon.entries[index]
                Surface(
                    modifier = Modifier.padding(4.dp).size(48.dp).clickable {
                        updateIcon(icon)
                    },
                    shape = CircleShape,
                    color = if (onExpenseIcon.value == icon) MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.5f
                    )
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon.toImageVector(),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DecorationBoxCostMain(textCost: Double, function: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E1E1E))
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
    ) {
        if (textCost.toString() == "0.0" || textCost.toString().isEmpty()
        ) {
            Text(
                text = "0.0",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        function()
    }
}

@Composable
fun DecorationBoxCost(textCost: String, function: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, shape = RoundedCornerShape(6.dp))
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
    ) {
        if (textCost == "0.0" || textCost.isEmpty()) {
            Text(
                text = "0.0",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        function()
    }
}

fun onClickFromConfirm(
    onExpenseName: MutableState<String>,
    textCost: String,
    uiState: TripUiState,
    addingNewOne: MutableState<Boolean>,
    onCreateExpense: (String, String, String, TravelIcon) -> Unit,
    onExpenseIcon: MutableState<TravelIcon>,
    onUpdateExpense: (String, String, String, TravelIcon) -> Unit,
    editExpense: TripExpenseUiState,
    updateDialog: () -> Unit
) {
    if ((onExpenseName.value.isBlank() || (textCost == "0.0" || textCost == "0" || textCost == "0." || textCost.isEmpty()) || uiState.optionalExpensesErrorAmount).not()) {
        if (addingNewOne.value) {
            onCreateExpense(
                Random.nextInt().toString(),
                onExpenseName.value,
                textCost,
                onExpenseIcon.value
            )
        } else {
            onUpdateExpense(
                editExpense.id,
                onExpenseName.value,
                textCost,
                onExpenseIcon.value
            )
        }
        updateDialog()
    }
}

@Composable
fun TotalEstimatedCostView(uiState: TripUiState) {
    SummaryCard(uiState = uiState)
}