package es.myvacations.myvacations.presentation.statistics

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import es.myvacations.myvacations.core.extensions.roundTo1Decimals
import es.myvacations.myvacations.core.extensions.shortCurrency
import es.myvacations.myvacations.core.extensions.shortenTitle
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.domain.model.displayName
import es.myvacations.myvacations.domain.model.flag
import es.myvacations.myvacations.domain.model.toName
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.utils.ChartItem
import es.myvacations.myvacations.presentation.utils.StatCard
import es.myvacations.myvacations.presentation.utils.iconColor
import es.myvacations.myvacations.presentation.utils.painter
import es.myvacations.myvacations.presentation.utils.toCurrencySymbol
import es.myvacations.myvacations.presentation.utils.toImageVector
import es.myvacations.myvacations.presentation.utils.toName
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.statistics_bycategory
import myvacations.shared.generated.resources.statistics_costpertrip
import myvacations.shared.generated.resources.statistics_countries
import myvacations.shared.generated.resources.statistics_notrips
import myvacations.shared.generated.resources.statistics_notrips_description
import myvacations.shared.generated.resources.statistics_priciestrip
import myvacations.shared.generated.resources.statistics_title
import myvacations.shared.generated.resources.statistics_totaldays
import myvacations.shared.generated.resources.statistics_totalspent
import myvacations.shared.generated.resources.total_trips
import myvacations.shared.generated.resources.trip_detail_overview_total_low
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilterStatus by remember { mutableStateOf(TripStatus.ALL) }

    val trips = remember(uiState.trips, selectedFilterStatus) {
        uiState.tripsTaken(selectedFilterStatus)
    }
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp),
            text = stringResource(Res.string.statistics_title),
            style = MaterialTheme.typography.headlineSmall
        )
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {
                CircularProgressIndicator()
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            LazyElementFilter(
                selectedFilterStatus,
                onFilterSelected = { selectedFilterStatus = it })
            if (trips.isNotEmpty()) {
                NotEmptyScreen(selectedFilterStatus, uiState, trips)
            } else {
                Spacer(modifier = Modifier.height(65.dp))
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Default.Luggage,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = stringResource(Res.string.statistics_notrips),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.statistics_notrips_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotEmptyScreen(
    selectedFilterStatus: TripStatus,
    uiState: StatisticsUiState,
    trips: List<TripUiState>
) {
    var selectedBar by remember {
        mutableStateOf(TripUiState())
    }
    val tripsCount = remember(uiState.trips, selectedFilterStatus) {
        uiState.tripsTakenNumber(selectedFilterStatus)
    }

    val totalSpent = remember(uiState.trips, selectedFilterStatus) {
        uiState.tripsTotalSpent(selectedFilterStatus)
    }
    val daysDuration = remember(uiState.trips, selectedFilterStatus) {
        uiState.tripsDaysDuration(selectedFilterStatus)
    }

    val countriesCount = remember(uiState.trips, selectedFilterStatus) {
        uiState.tripsCountriesInt(selectedFilterStatus)
    }

    val expenses = remember(uiState.trips, selectedFilterStatus) {
        uiState.tripsExpenses(selectedFilterStatus)
    }

    val priciest = remember(uiState.trips, selectedFilterStatus) {
        uiState.tripsPriciest(selectedFilterStatus)
    }

    LazyColumn(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            selectedBar = TripUiState()
        }) {
        item {
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatCard(
                        onStatisticsClick = { },
                        modifier = Modifier.weight(1f),
                        value = tripsCount.toString(),
                        label = stringResource(Res.string.total_trips),
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        color = Color(0xFF2B80FF),
                    )

                    StatCard(
                        onStatisticsClick = {},
                        modifier = Modifier.weight(1f),
                        value = totalSpent.shortCurrency() + " " + uiState.currency.toCurrencySymbol(),
                        label = stringResource(Res.string.statistics_totalspent),
                        icon = Icons.Default.Wallet,
                        color = Color(0xFFFF6060)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatCard(
                        onStatisticsClick = {},
                        modifier = Modifier.weight(1f),
                        value = daysDuration.toString()
                            .plus("d"),
                        label = stringResource(Res.string.statistics_totaldays),
                        icon = Icons.Default.LockClock,
                        color = Color(0xFFFFBE42),
                    )
                    StatCard(
                        {},
                        modifier = Modifier.weight(1f),
                        countriesCount.toString(),
                        label = stringResource(Res.string.statistics_countries),
                        icon = Icons.Default.Place,
                        color = Color(0xFF9C42FF),
                    )
                }
            }
        }
        item {
            if (trips.isNotEmpty()) {
                val maxCost =
                    (trips.maxOfOrNull { it.mainCost.roundTo1Decimals().toFloat() }
                        ?: 0f) * 1.15f
                val axisValues = remember(maxCost) {
                    val steps = 4
                    List(steps + 1) { index -> maxCost - (maxCost / steps) * index }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.elevatedCardColors()
                )
                {
                    Box {
                        if (selectedBar.id.isNotEmpty()) {
                            ElevatedCard(
                                modifier = Modifier.padding(top = 16.dp).zIndex(1f)
                                    .align(Alignment.TopCenter)
                                    .offset(y = 40.dp),
                                shape = RoundedCornerShape(20.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = selectedBar.titleTrip.shortenTitle()
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            stringResource(Res.string.trip_detail_overview_total_low) + ": " + selectedBar.mainCost
                                                .roundTo1Decimals()
                                                .shortCurrency() + selectedBar.currency.toCurrencySymbol()
                                        )

                                    }
                                }
                            }
                        }
                        Column(modifier = Modifier.fillMaxSize().height(200.dp)) {
                            Text(
                                modifier = Modifier.padding(top = 16.dp, start = 16.dp),
                                text = stringResource(Res.string.statistics_costpertrip),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(32.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                Column(
                                    modifier = Modifier.width(48.dp)
                                        .fillMaxHeight(0.85f),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.End
                                ) {
                                    axisValues.forEach { value ->
                                        Text(
                                            text = value.toDouble().roundTo1Decimals()
                                                .shortCurrency(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        // Líneas horizontales
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            repeat(5) {
                                                HorizontalDivider(
                                                    color = MaterialTheme.colorScheme.outline.copy(
                                                        alpha = 0.2f
                                                    ),
                                                    thickness = 1.dp
                                                )
                                            }
                                        }
                                        // Barras
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            trips.forEachIndexed { index, trip ->
                                                TripBar(
                                                    value = trip.mainCost.toFloat(),
                                                    maxValue = maxCost,
                                                    modifier = Modifier.width(40.dp)
                                                        .clickable(onClick = {
                                                            selectedBar = trip
                                                        }),
                                                    index = index,

                                                    )
                                            }
                                        }
                                    }
                                    HorizontalDivider()
                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {

                                        trips.forEach { trip ->
                                            Text(
                                                modifier = Modifier.width(56.dp),
                                                text = trip.titleTrip.shortenTitle(),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            val listOfItems = expenses.map { (travelIcon, amount) ->
                ChartItem(
                    name = travelIcon.toName(),
                    value = amount,
                    color = travelIcon.iconColor(),
                    icon = travelIcon.toImageVector(),
                    currency = uiState.currency
                )
            }
            if (listOfItems.isNotEmpty()) {
                Spacer(Modifier.height(32.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.elevatedCardColors()
                )
                {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.statistics_bycategory),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                @OptIn(ExperimentalKoalaPlotApi::class)
                                PieChart(
                                    modifier = Modifier.fillMaxHeight()
                                        .padding(end = 3.dp),
                                    values = listOfItems.map { it.value.toFloat() },
                                    holeSize = 0.8f,
                                    slice = { index ->
                                        DefaultSlice(
                                            color = listOfItems[index].color
                                        )
                                    },
                                    labelConnector = {})
                            }

                            Column(
                                modifier = Modifier.fillMaxSize().weight(1f),
                            ) {
                                listOfItems.forEach { items ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    color = items.color,
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = items.name.shortenTitle()
                                        )
                                        Text(
                                            modifier = Modifier,
                                            textAlign = TextAlign.End,
                                            text = items.value.roundTo1Decimals()
                                                .shortCurrency() + " " + uiState.currency.toCurrencySymbol()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.elevatedCardColors()
            )
            {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.statistics_priciestrip),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        priciest?.cover?.painter()?.let {
                            Image(
                                painter = it,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                                    .clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f))
                        {
                            Text(buildAnnotatedString {
                                append(priciest?.placeTrip?.flag)
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(
                                        priciest?.titleTrip?.shortenTitle(18)
                                    )
                                }
                            }, style = MaterialTheme.typography.titleMedium)
                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )
                            priciest?.placeTrip?.displayName()?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            priciest?.mainCost?.roundTo1Decimals()
                                ?.shortCurrency()?.let {
                                    Text(
                                        text = it + uiState.currency.toCurrencySymbol(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(65.dp)) }
    }
}

@Composable
fun LazyElementFilter(selectedFilterStatus: TripStatus, onFilterSelected: (TripStatus) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TripStatus.entries.toTypedArray().filter { it != TripStatus.FAVOURITE }) { filter ->
            Surface(
                modifier = Modifier.clickable {
                    onFilterSelected(filter)
                },
                shape = RoundedCornerShape(50),
                color = if (filter == selectedFilterStatus)
                    MaterialTheme.colorScheme.primary
                else
                    Color(0xFFF2F5F7)
            ) {
                Text(
                    filter.toName(),
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 10.dp
                    ),
                    color = if (filter == selectedFilterStatus)
                        Color.Black
                    else
                        Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TripBar(
    value: Float,
    maxValue: Float,
    modifier: Modifier = Modifier,
    index: Int
) {
    val targetPercentage =
        if (maxValue == 0f) 0f
        else value / maxValue

    val percentage by animateFloatAsState(
        targetValue = targetPercentage,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = index * 120,
            easing = FastOutSlowInEasing
        ),
        label = "TripBarAnimation"
    )

    Box(
        modifier = modifier.fillMaxHeight(percentage),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(percentage)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}