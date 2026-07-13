package es.myvacations.myvacations.presentation.tripdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.FamilyRestroom
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
import es.myvacations.myvacations.presentation.utils.ChartItem
import es.myvacations.myvacations.presentation.utils.DonutChart
import es.myvacations.myvacations.presentation.utils.ExpenseBreakdownItem
import es.myvacations.myvacations.presentation.utils.LegendItem
import es.myvacations.myvacations.presentation.utils.iconColor
import es.myvacations.myvacations.presentation.utils.toImageVector
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.close
import myvacations.shared.generated.resources.trip_detail_budget
import myvacations.shared.generated.resources.trip_detail_expenses
import myvacations.shared.generated.resources.trip_detail_expenses_budget_complete
import myvacations.shared.generated.resources.trip_detail_expenses_budget_exceed
import myvacations.shared.generated.resources.trip_detail_expenses_budgetnotused
import myvacations.shared.generated.resources.trip_detail_expenses_budgetused
import myvacations.shared.generated.resources.trip_detail_expenses_distribution
import myvacations.shared.generated.resources.trip_detail_expenses_legend
import myvacations.shared.generated.resources.trip_detail_legend_activities
import myvacations.shared.generated.resources.trip_detail_legend_exceed
import myvacations.shared.generated.resources.trip_detail_legend_food
import myvacations.shared.generated.resources.trip_detail_legend_maincost
import myvacations.shared.generated.resources.trip_detail_legend_remaining
import myvacations.shared.generated.resources.trip_detail_legend_transports
import myvacations.shared.generated.resources.trip_detail_legend_ubications
import myvacations.shared.generated.resources.trip_detail_legend_used
import myvacations.shared.generated.resources.trip_detail_overview_tripcost
import myvacations.shared.generated.resources.trip_detail_traveler_budget_low
import org.jetbrains.compose.resources.stringResource

@Preview(showBackground = true)
@Composable
fun ExpensesScreen(uiState: TripDetailUiState = TripDetailUiState()) {
    val legendOpen = remember { mutableStateOf(false) }

    val budgetList = when {
        uiState.tripUiState.mainBudget == 0.0 -> emptyList<ChartItem>()

        uiState.tripUiState.lowBudget == 0.0 -> listOf(
            ChartItem(
                value = uiState.tripUiState.mainBudget,
                color = Color(0xFFB45400),
                name = stringResource(Res.string.trip_detail_expenses_budget_complete),
                icon = Icons.Default.AttachMoney,
                currency = uiState.currency
            )
        )

        uiState.tripUiState.lowBudget > 15.0 -> listOf(
            ChartItem(
                value = uiState.tripUiState.remainingBudget,
                color = Color(0xFF11AC1F),
                name = stringResource(Res.string.trip_detail_expenses_budgetnotused),
                icon = Icons.Default.AttachMoney,
                currency = uiState.currency
            ), ChartItem(
                value = uiState.tripUiState.totalOptionalExpenses,
                color = Color.LightGray.copy(alpha = 0.2f),
                name = stringResource(Res.string.trip_detail_expenses_budgetused),
                icon = Icons.Default.AttachMoney,
                currency = uiState.currency
            )
        )

        uiState.tripUiState.remainingBudget < 0.0 -> listOf(
            ChartItem(
                value = uiState.tripUiState.totalOptionalExpenses,
                color = Color.Red,
                name = stringResource(Res.string.trip_detail_expenses_budget_exceed),
                icon = Icons.Default.AttachMoney,
                currency = uiState.currency
            )
        )

        else -> listOf(
            ChartItem(
                value = uiState.tripUiState.remainingBudget,
                color = Color(0xFFB45400),
                name = stringResource(Res.string.trip_detail_traveler_budget_low),
                icon = Icons.Default.AttachMoney,
                currency = uiState.currency
            ), ChartItem(
                value = uiState.tripUiState.totalOptionalExpenses,
                color = Color.LightGray.copy(alpha = 0.2f),
                name = stringResource(Res.string.trip_detail_expenses_budgetused),
                icon = Icons.Default.AttachMoney,
                currency = uiState.currency
            )
        )
    }

    val chartItems = buildList {
        add(
            ChartItem(
                value = uiState.tripUiState.mainCost,
                color = Color(0xFF1A557B),
                name = stringResource(Res.string.trip_detail_overview_tripcost),
                icon = Icons.Default.FamilyRestroom,
                currency = uiState.tripUiState.currency
            )
        )

        addAll(
            uiState.tripUiState.optionalExpenses.map { expense ->
                ChartItem(
                    name = expense.name,
                    value = expense.amount,
                    color = expense.icon.iconColor(),
                    icon = expense.icon.toImageVector(),
                    currency = uiState.tripUiState.currency
                )
            })
    }

    val total = uiState.tripUiState.mainCost


    Spacer(modifier = Modifier.height(24.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        if (legendOpen.value) {
            AlertDialog(
                onDismissRequest = { legendOpen.value = false },
                title = { Text(stringResource(Res.string.trip_detail_expenses_legend)) },
                text = {
                    Column {
                        Text(
                            text = stringResource(Res.string.trip_detail_budget),
                            style = MaterialTheme.typography.titleSmall
                        )

                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        LegendItem(
                            Color(0xFF11AC1F),
                            stringResource(Res.string.trip_detail_legend_remaining)
                        )

                        LegendItem(
                            Color(0xFFB45400),
                            stringResource(Res.string.trip_detail_legend_used)
                        )

                        LegendItem(
                            Color.Red,
                            stringResource(Res.string.trip_detail_legend_exceed)
                        )

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = stringResource(Res.string.trip_detail_expenses),
                            style = MaterialTheme.typography.titleSmall
                        )

                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        LegendItem(
                            Color(0xFF1A557B),
                            stringResource(Res.string.trip_detail_legend_maincost)
                        )

                        LegendItem(
                            Color(0xFF09D794),
                            stringResource(Res.string.trip_detail_legend_ubications)

                        )

                        LegendItem(
                            Color(0xFF06B6D4),
                            stringResource(Res.string.trip_detail_legend_transports)
                        )

                        LegendItem(
                            Color(0xFFF59E0B),
                            stringResource(Res.string.trip_detail_legend_food)
                        )

                        LegendItem(
                            Color(0xFF93FF86),
                            stringResource(Res.string.trip_detail_legend_activities)
                        )
                    }
                },
                confirmButton = {
                    Text(
                        modifier = Modifier.clickable(onClick = {
                            legendOpen.value = false
                        }),
                        text = stringResource(Res.string.close),
                    )
                }
            )
        }
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.trip_detail_expenses_distribution),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(20.dp))
            DonutChart(uiState.tripUiState, budgetList, chartItems, total)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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
    Spacer(Modifier.height(8.dp))

    budgetList.firstOrNull().let {
        if (it != null) {
            val percentage = (it.value / uiState.tripUiState.mainBudget) * 100
            ExpenseBreakdownItem(
                true,
                item = it,
                percentage = percentage,
                totalBudget = uiState.tripUiState.mainBudget
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    chartItems.forEach { chartItems ->
        val percentage = (chartItems.value / total) * 100
        ExpenseBreakdownItem(item = chartItems, percentage = percentage)
        Spacer(modifier = Modifier.height(8.dp))
    }
}