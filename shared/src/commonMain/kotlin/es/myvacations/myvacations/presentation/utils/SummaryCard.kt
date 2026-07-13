package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.extensions.shortCurrency
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.close
import myvacations.shared.generated.resources.new_trip_legend_activities
import myvacations.shared.generated.resources.new_trip_legend_budget_remaining_description
import myvacations.shared.generated.resources.new_trip_legend_budget_remaining_title
import myvacations.shared.generated.resources.new_trip_legend_budgetsummary
import myvacations.shared.generated.resources.new_trip_legend_categories
import myvacations.shared.generated.resources.new_trip_legend_example
import myvacations.shared.generated.resources.new_trip_legend_example_title
import myvacations.shared.generated.resources.new_trip_legend_expected_cost_description
import myvacations.shared.generated.resources.new_trip_legend_expected_cost_title
import myvacations.shared.generated.resources.new_trip_legend_food
import myvacations.shared.generated.resources.new_trip_legend_main_cost
import myvacations.shared.generated.resources.new_trip_legend_places
import myvacations.shared.generated.resources.new_trip_legend_title
import myvacations.shared.generated.resources.new_trip_legend_transport
import myvacations.shared.generated.resources.new_trip_legend_travelers_description
import myvacations.shared.generated.resources.new_trip_legend_travelers_title
import myvacations.shared.generated.resources.new_trip_my_expected_budget
import myvacations.shared.generated.resources.new_trip_my_expected_cost
import myvacations.shared.generated.resources.trip_detail_expenses_legend
import myvacations.shared.generated.resources.trip_detail_overview_person
import org.jetbrains.compose.resources.stringResource

@Preview(showBackground = true)
@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    uiState: TripUiState = TripUiState()
) {
    val legendOpen = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    if (legendOpen.value) {
        AlertDialog(
            onDismissRequest = { legendOpen.value = false },
            title = { Text(stringResource(Res.string.new_trip_legend_title)) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Budget summary
                    Text(
                        text = stringResource(Res.string.new_trip_legend_budgetsummary),
                        style = MaterialTheme.typography.titleSmall
                    )

                    HorizontalDivider(Modifier.padding(vertical = 8.dp))

                    Text(
                        text = stringResource(Res.string.new_trip_legend_expected_cost_title),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = stringResource(Res.string.new_trip_legend_expected_cost_description),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = stringResource(Res.string.new_trip_legend_budget_remaining_title),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = stringResource(Res.string.new_trip_legend_budget_remaining_description),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(20.dp))

                    // Travelers
                    Text(
                        text = stringResource(Res.string.new_trip_legend_travelers_title),
                        style = MaterialTheme.typography.titleSmall
                    )

                    HorizontalDivider(Modifier.padding(vertical = 8.dp))

                    Text(
                        text = stringResource(Res.string.new_trip_legend_travelers_description),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = stringResource(Res.string.new_trip_legend_example_title),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = stringResource(
                                Res.string.new_trip_legend_example,
                                uiState.currency.toCurrencySymbol(),
                                uiState.currency.toCurrencySymbol(),
                                uiState.currency.toCurrencySymbol(),
                                uiState.currency.toCurrencySymbol()
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Expense categories
                    Text(
                        text = stringResource(Res.string.new_trip_legend_categories),
                        style = MaterialTheme.typography.titleSmall
                    )

                    HorizontalDivider(Modifier.padding(vertical = 8.dp))

                    LegendItem(
                        Color(0xFF1A557B),
                        stringResource(Res.string.new_trip_legend_main_cost)
                    )

                    LegendItem(
                        Color(0xFF09D794),
                        stringResource(Res.string.new_trip_legend_places)
                    )

                    LegendItem(
                        Color(0xFF06B6D4),
                        stringResource(Res.string.new_trip_legend_transport)
                    )

                    LegendItem(
                        Color(0xFFF59E0B),
                        stringResource(Res.string.new_trip_legend_food)
                    )

                    LegendItem(
                        Color(0xFF93FF86),
                        stringResource(Res.string.new_trip_legend_activities)
                    )
                }
            },
            confirmButton = {
                Text(
                    modifier = Modifier.clickable {
                        legendOpen.value = false
                    },
                    text = stringResource(Res.string.close)
                )
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFFB7D3D7)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.new_trip_my_expected_cost),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(Res.string.new_trip_my_expected_budget),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.individualCost.shortCurrency() + " " + uiState.currency.toCurrencySymbol(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF007C91)
                )
                Text(
                    text = if (uiState.totalOptionalExpenses != 0.0) " (" + uiState.costPerPerson.shortCurrency() + " " + uiState.currency.toCurrencySymbol() + " + " + uiState.totalOptionalExpenses.shortCurrency() + " " + uiState.currency.toCurrencySymbol() + ")" else "",
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (uiState.mainBudget != 0.0 && uiState.totalOptionalExpenses != 0.0) uiState.remainingBudget.shortCurrency() + " " + uiState.currency.toCurrencySymbol() else "-",
                    style = MaterialTheme.typography.headlineSmall,
                    color = when {
                        (uiState.mainBudget == 0.0 || uiState.totalOptionalExpenses == 0.0) -> Color(
                            0xFF007C91
                        )

                        uiState.lowBudget > 15.0 -> Color(0xFF11AC1F)
                        uiState.remainingBudget < 0.0 -> Color.Red
                        else -> Color(0xFFB45400)
                    }
                )

                Text(
                    text = if (uiState.mainBudget != 0.0 && uiState.totalOptionalExpenses != 0.0) " (" + uiState.mainBudget.shortCurrency() + " " + uiState.currency.toCurrencySymbol() + " - " + uiState.totalOptionalExpenses.shortCurrency() + " " + uiState.currency.toCurrencySymbol() + ")" else "",
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.mainCost.shortCurrency() + " " + uiState.currency.toCurrencySymbol() + " • " + uiState.travelers.toString() + " " + stringResource(
                        Res.string.trip_detail_overview_person
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF007C91)
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(Res.string.trip_detail_expenses_legend),
                    modifier = Modifier.clickable(onClick = {
                        legendOpen.value = true
                    }),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}