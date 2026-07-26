package es.myvacations.myvacations.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
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
import es.myvacations.myvacations.utils.createBudgetProgressBitmap
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
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(
                                imageProvider = ImageProvider(
                                    R.drawable.notrips
                                ),
                                alpha = 0.5f,
                                contentScale = ContentScale.Crop
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = context.getString(R.string.notravel),
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = ColorProvider(
                                    day = Color(0xFFFFFFFF),
                                    night = Color(0xFFFFFFFF)
                                )
                            )
                        )
                    }
                }

                trip.tripStatus == TripStatus.PLANNED -> {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(
                                imageProvider = ImageProvider(
                                    R.drawable.notrips
                                ),
                                alpha = 0.5f,
                                contentScale = ContentScale.Crop
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = context.getString(
                                R.string.nexttravel,
                                trip.remainingDaysForStart
                            ),
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = ColorProvider(
                                    day = Color(0xFFFFFFFF),
                                    night = Color(0xFFFFFFFF)
                                )
                            )
                        )
                    }
                }

                else -> {
                    val color = when {
                        trip.lowBudget > 15.0 -> Color(0xFF11AC1F)
                        trip.remainingBudget < 0.0 -> Color(0xFFFF0000)
                        else -> Color(0xFFB45400)
                    }

                    val spent = trip.totalOptionalExpenses
                    val budget = trip.mainBudget

                    val percentage = if (budget > 0) {
                        ((spent / budget) * 100).toInt()
                    } else {
                        0
                    }

                    val progressBitmap = createBudgetProgressBitmap(
                        percentage = percentage,
                        color = color.toArgb()
                    )

                    Column(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .background(R.color.background)
                    ) {

                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = GlanceModifier.defaultWeight()
                            ) {
                                Text(
                                    text = trip.titleTrip,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorProvider(
                                            day = Color(0xFFFFFFFF),
                                            night = Color(0xFFFFFFFF)
                                        )
                                    )
                                )

                                Text(
                                    text = context.getString(
                                        trip.placeTrip.glanceStringRes()
                                    ),
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = ColorProvider(
                                            day = Color(0xFFFFFFFF),
                                            night = Color(0xFFFFFFFF)
                                        )
                                    )
                                )
                            }
                            Text(
                                text = if (trip.remainingDaysOfTravel == 0) context.getString(R.string.nodaysleft) else context.getString(
                                    R.string.daysleft,
                                    trip.remainingDaysOfTravel
                                ),
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorProvider(
                                        day = Color(0xFFFFFFFF),
                                        night = Color(0xFFFFFFFF)
                                    )
                                )
                            )
                        }

                        Spacer(
                            modifier = GlanceModifier.height(12.dp)
                        )
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Image(
                                provider = ImageProvider(progressBitmap),
                                contentDescription = null,
                                modifier = GlanceModifier.size(64.dp)
                            )

                            Spacer(
                                modifier = GlanceModifier.width(16.dp)
                            )

                            Column(
                                modifier = GlanceModifier.defaultWeight()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = "${spent.toInt()}" +
                                                context.getString(
                                                    trip.currency.glanceStringRes()
                                                ),
                                        style = TextStyle(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorProvider(
                                                day = Color(0xFFFFFFFF),
                                                night = Color(0xFFFFFFFF)
                                            )
                                        )
                                    )

                                    Spacer(
                                        modifier = GlanceModifier.width(4.dp)
                                    )

                                    Text(
                                        text = "/ ${budget.toInt()}" +
                                                context.getString(
                                                    trip.currency.glanceStringRes()
                                                ),
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = ColorProvider(
                                                day = Color(0xA8B6B6FF),
                                                night = Color(0xA8B6B6FF)
                                            )
                                        )
                                    )
                                    Spacer(
                                        modifier = GlanceModifier.width(4.dp)
                                    )
                                    Text(
                                        text = "• " + context.getString(
                                            R.string.expensein,
                                            trip.optionalExpenses.size
                                        ),
                                        style = TextStyle(
                                            fontSize = 10.sp,
                                            color = ColorProvider(
                                                day = Color(0xA8B6B6FF),
                                                night = Color(0xA8B6B6FF)
                                            )
                                        )
                                    )
                                }

                                Spacer(
                                    modifier = GlanceModifier.height(3.dp)
                                )
                                LoadTextBudget(context, trip, color)

                                Spacer(
                                    modifier = GlanceModifier.height(7.dp)
                                )

                                LinearProgressIndicator(
                                    progress = (percentage / 100f)
                                        .coerceIn(0f, 1f),
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    color = ColorProvider(
                                        day = color,
                                        night = color
                                    )
                                )
                            }
                        }

                        Spacer(
                            modifier = GlanceModifier.defaultWeight()
                        )

                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val launchIntentExpense = context.packageManager
                                .getLaunchIntentForPackage(context.packageName)
                            val launchIntentDetail = context.packageManager
                                .getLaunchIntentForPackage(context.packageName)
                            if (launchIntentExpense != null) {
                                Button(
                                    text = context.getString(
                                        R.string.addExpense
                                    ),
                                    onClick = actionStartActivity(
                                        launchIntentExpense.apply {
                                            putExtra("trip_id", trip.id)
                                            putExtra("widget_action", "add_expense")
                                        }
                                    ),
                                    modifier = GlanceModifier.defaultWeight(),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = ColorProvider(
                                            day = Color(0xFF006773),
                                            night = Color(0xFF006773)
                                        ),
                                        contentColor = ColorProvider(
                                            day = Color.White,
                                            night = Color.White
                                        )
                                    )
                                )
                            }
                            Spacer(
                                modifier = GlanceModifier.width(8.dp)
                            )
                            if (launchIntentDetail != null) {
                                Button(
                                    text = context.getString(
                                        R.string.watchtravel
                                    ),
                                    onClick = actionStartActivity(
                                        launchIntentDetail.apply {
                                            putExtra("trip_id", trip.id)
                                            putExtra("widget_action", "trip_detail")
                                        }
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = ColorProvider(
                                            day = Color.Transparent,
                                            night = Color.Transparent
                                        ),
                                        contentColor = ColorProvider(
                                            day = Color(0xA8B6B6FF),
                                            night = Color(0xA8B6B6FF)
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadTextBudget(context: Context, trip: TripUiState, color: Color) {
    when {
        trip.lowBudget > 15.0 -> {
            Text(
                text = context.getString(
                    R.string.budgetremaining,
                    trip.remainingBudget,
                    context.getString(
                        trip.currency.glanceStringRes()
                    )
                ),
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(
                        day = color,
                        night = color
                    )
                )
            )
        }

        trip.lowBudget == 0.0 -> {
            Text(
                text = context.getString(
                    R.string.nobudget
                ),
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(
                        day = color,
                        night = color
                    )
                )
            )
        }

        trip.remainingBudget < 0.0 -> {
            Text(
                text = context.getString(
                    R.string.budgetexceed,
                    trip.remainingBudget.times(-1),
                    context.getString(
                        trip.currency.glanceStringRes()
                    )
                ),
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(
                        day = color,
                        night = color
                    )
                )
            )
        }

        else -> {
            Text(
                text = context.getString(
                    R.string.budgetlow
                ),
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(
                        day = color,
                        night = color
                    )
                )
            )
        }
    }
}