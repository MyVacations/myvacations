package es.myvacations.myvacations.presentation.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.extensions.roundTo1Decimals
import es.myvacations.myvacations.core.extensions.shortCurrencyWhen1000
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import io.github.koalaplot.core.pie.DefaultSlice
import io.github.koalaplot.core.pie.PieChart
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun DonutChart(
    uiState: TripUiState = TripUiState(),
    chartItems: List<ChartItem> = emptyList(),
    listItemsBudget: List<ChartItem> = emptyList(),
    total: Double,
    totalBudget: Double
) {
    var selectedInternalSlice by remember {
        mutableStateOf<Int?>(null)
    }
    var selectedExternalSlice by remember {
        mutableStateOf<Int?>(null)
    }

    Box(
        modifier = Modifier.fillMaxWidth().clickable(
            interactionSource = MutableInteractionSource(),
            indication = null
        ) {
            selectedInternalSlice = null
            selectedExternalSlice = null
        }, contentAlignment = Alignment.Center
    ) {
        PieChart(
            modifier = Modifier.fillMaxWidth(),
            holeSize = 0.82f,
            values = listItemsBudget.map { it.value.toFloat() },
            slice = { index ->
                val selected = selectedExternalSlice == index
                DefaultSlice(
                    color = if (selected) listItemsBudget[index].color.copy(alpha = 0.85f)
                    else listItemsBudget[index].color,
                    border = if (selected) BorderStroke(5.dp, Color.White)
                    else null,
                    clickable = true,
                    onClick = {
                        selectedExternalSlice = if (selectedExternalSlice == index) null else index
                        selectedInternalSlice = null
                    })
            },
            labelConnector = {},
            holeContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    PieChart(
                        modifier = Modifier.fillMaxSize().padding(end = 3.dp),
                        values = chartItems.map { it.value.toFloat() },
                        holeSize = 0.72F,
                        slice = { index ->
                            val selected = selectedInternalSlice == index
                            DefaultSlice(
                                color = if (selected) chartItems[index].color.copy(alpha = 0.85f)
                                else chartItems[index].color,
                                border = if (selected) BorderStroke(5.dp, Color.White)
                                else null,
                                clickable = true,
                                onClick = {
                                    selectedInternalSlice =
                                        if (selectedInternalSlice == index) null else index
                                    selectedExternalSlice = null
                                })
                        },
                        labelConnector = {})
                }
            })

        Box(
            modifier = Modifier.size(180.dp), contentAlignment = Alignment.Center
        ) {
            if (selectedExternalSlice != null) {
                AnimatedContent(
                    targetState = selectedExternalSlice, label = "SelectedSliceExternal"
                ) { index ->
                    if (index != null) {
                        val item = listItemsBudget[index]
                        val percentage = (item.value / totalBudget) * 100

                        Card(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = item.name, color = item.color
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(item.value.shortCurrencyWhen1000() + " " + uiState.currency.toCurrencySymbol() + " / " + percentage.roundTo1Decimals() + "%")

                                }
                            }
                        }
                    }
                }
            }
            if (selectedInternalSlice != null) {
                AnimatedContent(
                    targetState = selectedInternalSlice, label = "SelectedSliceInternal"
                ) { index ->
                    if (index != null) {
                        val item = chartItems[index]
                        val percentage = (item.value / total) * 100

                        Card(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = item.name, color = item.color
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(item.value.shortCurrencyWhen1000() + " " + uiState.currency.toCurrencySymbol() + " / " + percentage.roundTo1Decimals() + "%")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}