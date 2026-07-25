package es.myvacations.myvacations.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.kizitonwose.calendar.core.now
import es.myvacations.myvacations.data.repository.ActiveTripCoverKey
import es.myvacations.myvacations.data.repository.ActiveTripEndDateKey
import es.myvacations.myvacations.data.repository.ActiveTripExpensesKey
import es.myvacations.myvacations.data.repository.ActiveTripFavouriteKey
import es.myvacations.myvacations.data.repository.ActiveTripIdKey
import es.myvacations.myvacations.data.repository.ActiveTripMainBudgetKey
import es.myvacations.myvacations.data.repository.ActiveTripMainCostKey
import es.myvacations.myvacations.data.repository.ActiveTripPlaceKey
import es.myvacations.myvacations.data.repository.ActiveTripStartDateKey
import es.myvacations.myvacations.data.repository.ActiveTripTitleKey
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.utils.TripExpenseUiState
import es.myvacations.myvacations.shared.R
import es.myvacations.myvacations.utils.glanceImageRes
import es.myvacations.myvacations.utils.glanceStringRes
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

class MyVacationWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val preferences = currentState<Preferences>()
            val id = preferences[ActiveTripIdKey]

            val trip = if (id != null) TripUiState(
                id = id,
                titleTrip = preferences[ActiveTripTitleKey].orEmpty(),
                placeTrip = preferences[ActiveTripPlaceKey]
                    ?.let { runCatching { Country.valueOf(it) }.getOrNull() }
                    ?: Country.SPAIN,
                startDate = preferences[ActiveTripStartDateKey]
                    ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                    ?: LocalDate.now(),
                endDate = preferences[ActiveTripEndDateKey]
                    ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                    ?: LocalDate.now(),
                mainCost = preferences[ActiveTripMainCostKey] ?: 0.0,
                mainBudget = preferences[ActiveTripMainBudgetKey] ?: 0.0,
                cover = preferences[ActiveTripCoverKey]
                    ?.let { runCatching { TripCover.valueOf(it) }.getOrNull() }
                    ?: TripCover.BARCELONA,
                optionalExpenses = preferences[ActiveTripExpensesKey]?.let {
                    runCatching {
                        Json.decodeFromString<List<TripExpenseUiState>>(it)
                    }.getOrNull()
                } ?: emptyList(),
                favourite = preferences[ActiveTripFavouriteKey] ?: false
            )
            else null

            WidgetContentFromState(
                context,
                trip
            )
        }
    }

    @Composable
    private fun WidgetContentFromState(
        context: Context,
        tripUiState: TripUiState?
    ) {
        WidgetContent(context, tripUiState)
    }

    @Composable
    private fun WidgetContent(context: Context, trip: TripUiState?) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                trip == null -> {
                    Text(context.getString(R.string.notravel))
                }

                trip.tripStatus == TripStatus.PLANNED -> Text(
                    context.getString(
                        R.string.nexttravel,
                        trip.remainingDays
                    )
                )

                else -> {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(
                                imageProvider = ImageProvider(
                                    trip.cover.glanceImageRes()
                                ),
                                alpha = 0.3f,
                                contentScale = ContentScale.Crop
                            )
                    ) {
                        Column(
                            modifier = GlanceModifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = trip.titleTrip,
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(GlanceModifier.height(4.dp))
                            if (trip.totalOptionalExpenses != 0.0 && trip.mainBudget != 0.0) {
                                Text(
                                    text = "${trip.totalOptionalExpenses.toInt()}" + "${
                                        context.getString(
                                            trip.currency.glanceStringRes()
                                        )
                                    } / ${trip.mainBudget.toInt()}" + context.getString(trip.currency.glanceStringRes()),
                                    style = TextStyle(
                                        fontSize = 13.sp
                                    )
                                )
                                Spacer(GlanceModifier.height(8.dp))
                                Text(
                                    text = context.getString(
                                        R.string.expensein,
                                        trip.optionalExpenses.size
                                    ),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            } else {
                                Text(
                                    text = context.getString(R.string.noexpenseorbudget),
                                    style = TextStyle(
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            Spacer(GlanceModifier.height(16.dp))

                            Text(
                                text = "Añadir gasto",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

            }
        }

    }

    @Composable
    private fun ExpenseButton(
        text: String
    ) {
        Button(
            text = text,
            onClick = actionRunCallback<EmptyAction>()
        )
    }
}

class EmptyAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) = Unit
}