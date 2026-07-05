package es.myvacations.myvacations.presentation.createedittrip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.displayName
import es.myvacations.myvacations.domain.model.flag
import es.myvacations.myvacations.presentation.utils.AppDatePickerDialog
import es.myvacations.myvacations.presentation.utils.AppDropDown
import es.myvacations.myvacations.presentation.utils.ExpenseItem
import es.myvacations.myvacations.presentation.utils.NumberPicker
import es.myvacations.myvacations.presentation.utils.SummaryCard
import es.myvacations.myvacations.presentation.utils.TravelIcon
import es.myvacations.myvacations.presentation.utils.painter
import es.myvacations.myvacations.presentation.utils.toCurrencySymbol
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.accept
import myvacations.shared.generated.resources.cancel
import myvacations.shared.generated.resources.edt_trip
import myvacations.shared.generated.resources.new_trip_D_G
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
import myvacations.shared.generated.resources.new_trip_end_date_require
import myvacations.shared.generated.resources.new_trip_error
import myvacations.shared.generated.resources.new_trip_estimated_total
import myvacations.shared.generated.resources.new_trip_optional_expenses
import myvacations.shared.generated.resources.new_trip_save_trip
import myvacations.shared.generated.resources.new_trip_start_date
import myvacations.shared.generated.resources.new_trip_start_date_require
import myvacations.shared.generated.resources.new_trip_title
import myvacations.shared.generated.resources.new_trip_title_require
import myvacations.shared.generated.resources.new_trip_travelers
import myvacations.shared.generated.resources.new_trip_traveling_days
import myvacations.shared.generated.resources.new_trip_trip_country
import myvacations.shared.generated.resources.new_trip_trip_title
import myvacations.shared.generated.resources.new_trip_trip_title_example
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddEditTripScreen(
    tripId: String,
    onDismiss: () -> Unit,
    viewModel: CreateEditTripsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            onDaysTravelingChange = viewModel::updateDaysTraveling,
            onTravelersChange = viewModel::updateTravelers,
            onMainCostChange = viewModel::updateMainCost,
            onMainBudgetChange = viewModel::updateMainBudget,
            toggleOptionalExpenses = viewModel::toggleOptionalExpenses,
            addExpense = viewModel::addExpense,
            onDeleteExpense = viewModel::deleteExpense,
            updateExpenseName = viewModel::updateExpenseName,
            updateExpenseAmount = viewModel::updateExpenseAmount,
            updateExpenseIcon = viewModel::updateExpenseIcon,
            onSave = viewModel::saveTrip,
            clearUI = viewModel::clearUi
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
    addExpense: () -> Unit = {},
    onDeleteExpense: (String) -> Unit = {},
    updateExpenseName: (String, String) -> Unit = { _, _ -> },
    updateExpenseAmount: (String, String) -> Unit = { _, _ -> },
    updateExpenseIcon: (String, TravelIcon) -> Unit = { _, _ -> },
    onSave: () -> Unit = {},
    clearUI: () -> Unit = {}
) {
    SystemBackHandler {
        onDismiss()
    }
    val dialogClear = remember { mutableStateOf(false) }
    val dialogSaveNotReady = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (uiState.editMode) Res.string.edt_trip else Res.string.new_trip_title)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                actions = {
                    TextButton(onClick = { dialogClear.value = true }) {
                        Text(
                            text = stringResource(Res.string.new_trip_clear),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        if ((uiState.titleTrip.isEmpty() || uiState.startDate == null || uiState.endDate == null || (uiState.mainCost == 0.0 || uiState.mainCost == null) || (uiState.startDate > uiState.endDate)).not()) {
                            onSave()
                            onDismiss()
                        } else {
                            dialogSaveNotReady.value = true
                        }
                    }) {
                        Text(stringResource(Res.string.new_trip_save_trip))
                    }
                }
            )
        }
    ) { padding ->
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                DestinationView(uiState, onTitleTripChange, onCountrySelected, onCoverSelected)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                DatesView(uiState, onStartDateChange, onEndDateChange)
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
                    onAddExpense = addExpense,
                    onDeleteExpense = onDeleteExpense,
                    onUpdateExpenseName = updateExpenseName,
                    onUpdateExpenseAmount = updateExpenseAmount,
                    onUpdateExpenseIcon = updateExpenseIcon
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                TotalEstimatedCostView(uiState)
                Spacer(modifier = Modifier.height(16.dp))
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
    onCoverSelected: (TripCover) -> Unit = {}
) {
    var showCoverDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.new_trip_destination),
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.new_trip_trip_title),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))
        val maxTextLength = 30

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
                .height(140.dp)
                .clickable {
                    showCoverDialog = true
                }
        ) {
            Image(
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
    onEndDateChange: (LocalDate) -> Unit = {},
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
                            it?.let { date ->
                                "${
                                    date.day.toString().padStart(2, '0')
                                }/${date.month.number.toString().padStart(2, '0')}/${date.year}"
                            }
                        } ?: "dd/mm/aaaa",
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            }
            if (uiState.startDate == null || (uiState.startDate != null && uiState.endDate != null && uiState.startDate > uiState.endDate)) {
                Text(
                    text = stringResource(Res.string.new_trip_start_date_require),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
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
                            it?.let { date ->
                                "${
                                    date.day.toString().padStart(2, '0')
                                }/${date.month.number.toString().padStart(2, '0')}/${date.year}"
                            }
                        } ?: "dd/mm/aaaa",
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            }
            if (uiState.endDate == null || (uiState.startDate != null && uiState.endDate != null && uiState.startDate > uiState.endDate)) {
                Text(
                    text = stringResource(Res.string.new_trip_end_date_require),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    if (showDatePickerEnd.value) AppDatePickerDialog(
        onDismiss = { showDatePickerEnd.value = false },
        onDateSelected = { onEndDateChange(it) })
    if (showDatePickerStart.value) AppDatePickerDialog(
        onDismiss = { showDatePickerStart.value = false },
        onDateSelected = onStartDateChange
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview(showBackground = true)
@Composable
fun DurationAndGroupView(
    uiState: TripUiState = TripUiState(),
    onDaysTravelingChange: (Int) -> Unit = {},
    onTravelersChange: (Int) -> Unit = {}
) {
    Text(
        text = stringResource(Res.string.new_trip_D_G),
        style = MaterialTheme.typography.labelMedium,
        letterSpacing = 2.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.new_trip_duration),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (uiState.totalDays <= 0) "-" else uiState.totalDays.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.new_trip_days),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.new_trip_traveling_days),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            NumberPicker(
                value = uiState.daysTraveling,
                onValueChange = onDaysTravelingChange,
                min = 0
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.new_trip_travelers),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            NumberPicker(
                value = uiState.travelers,
                onValueChange = onTravelersChange,
                min = 1
            )
        }
    }
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
                    if (uiState.mainCost.toString() == "0.0" || uiState.mainCost.toString().isEmpty()) {
                        Text(
                            text = "0.0",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    innerTextField()
                }
            }
        )
        if (uiState.mainCost == null || uiState.mainCost == 0.0) {
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
                    if (uiState.mainBudget.toString() == "0.0" || uiState.mainBudget.toString().isEmpty()) {
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
    onUpdateExpenseIcon: (String, TravelIcon) -> Unit = { _, _ -> },
    onToggleExpanded: () -> Unit = {},
    onAddExpense: () -> Unit = {},
    onDeleteExpense: (String) -> Unit = {},
    onUpdateExpenseName: (String, String) -> Unit = { _, _ -> },
    onUpdateExpenseAmount: (String, String) -> Unit = { _, _ -> },
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
        if (uiState.optionalExpensesExpanded) {
            uiState.optionalExpenses.forEach { expense ->
                ExpenseItem(
                    expense,
                    onDelete = { onDeleteExpense(expense.id) },
                    onUpdateExpenseName,
                    onUpdateExpenseIcon,
                    onUpdateExpenseAmount
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            TextButton(
                onClick = onAddExpense
            ) {
                Text(stringResource(Res.string.new_trip_add_expense))
            }
        }
    }
}

@Composable
fun TotalEstimatedCostView(uiState: TripUiState) {
    SummaryCard(
        title = stringResource(Res.string.new_trip_estimated_total),
        value = uiState.totalCost.toString() + " " + uiState.currency.toCurrencySymbol()
    )
}