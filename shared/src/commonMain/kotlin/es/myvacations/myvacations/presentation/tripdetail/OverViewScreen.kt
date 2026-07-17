package es.myvacations.myvacations.presentation.tripdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.presentation.utils.SummaryRow
import es.myvacations.myvacations.presentation.utils.toCurrencySymbol
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.trip_detail_overview_aditionalexpenses
import myvacations.shared.generated.resources.trip_detail_overview_day
import myvacations.shared.generated.resources.trip_detail_overview_perday
import myvacations.shared.generated.resources.trip_detail_overview_perperson
import myvacations.shared.generated.resources.trip_detail_overview_person
import myvacations.shared.generated.resources.trip_detail_overview_stayday
import myvacations.shared.generated.resources.trip_detail_overview_total
import myvacations.shared.generated.resources.trip_detail_overview_total_low
import myvacations.shared.generated.resources.trip_detail_overview_traveldays
import myvacations.shared.generated.resources.trip_detail_overview_travelers
import myvacations.shared.generated.resources.trip_detail_overview_tripcost
import myvacations.shared.generated.resources.trip_detail_traveler_costyou
import org.jetbrains.compose.resources.stringResource

@Composable
fun OverViewScreen(uiState: TripDetailUiState) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewInfoCard(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.trip_detail_overview_total),
                value = uiState.tripUiState.totalDays.toString() + " " + stringResource(Res.string.trip_detail_overview_day),
                icon = Icons.Default.DateRange
            )

            OverviewInfoCard(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.trip_detail_overview_travelers),
                value = uiState.tripUiState.travelers.toString() + " " + stringResource(Res.string.trip_detail_overview_person),
                icon = Icons.Default.People
            )
        }

        CostSummaryCard(
            uiState
        )
    }
}

@Composable
private fun OverviewInfoCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0B7285),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CostSummaryCard(
    uiState: TripDetailUiState = TripDetailUiState(),
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "Cost Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            SummaryRow(
                title = stringResource(Res.string.trip_detail_overview_tripcost),
                value = uiState.tripUiState.mainCost.toString() + " " + uiState.currency.toCurrencySymbol()
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(
                title = stringResource(Res.string.trip_detail_overview_aditionalexpenses),
                value = uiState.tripUiState.totalOptionalExpenses.toString() + " " + uiState.currency.toCurrencySymbol()
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            SummaryRow(
                title = stringResource(Res.string.trip_detail_overview_total_low),
                value = uiState.tripUiState.mainCost.toString() + " " + uiState.currency.toCurrencySymbol(),
                highlight = true,
                bold = true
            )
        }
    }
}