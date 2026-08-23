package es.myvacations.myvacations.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentWidth
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import es.myvacations.myvacations.data.repository.WidgetEventPreferencesKey
import es.myvacations.myvacations.data.repository.WidgetEventResult
import es.myvacations.myvacations.data.repository.WidgetNextUpdatePreferencesKey
import es.myvacations.myvacations.domain.usecase.eventsusecase.PlacesWidgetObserverUseCase
import es.myvacations.myvacations.shared.R
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent

class PlacesWidget : GlanceAppWidget(),
    KoinComponent {
    override val stateDefinition = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val preferences = currentState<Preferences>()
            val eventJson = preferences[WidgetEventPreferencesKey]
            val timer = preferences[WidgetNextUpdatePreferencesKey] ?: System.currentTimeMillis()
            val widgetEventResult =
                eventJson?.let {
                    runCatching {
                        Json.decodeFromString<WidgetEventResult>(it)
                    }.getOrElse {
                        WidgetEventResult.Error
                    }
                } ?: WidgetEventResult.Error

            WidgetContent(
                context,
                widgetEventResult,
                timer
            )
        }
    }

    @Composable
    private fun WidgetContent(
        context: Context,
        widgetEventResult: WidgetEventResult,
        timer: Long,
    ) {
        when (widgetEventResult) {
            WidgetEventResult.Loading -> LocationLoadingWidget()
            WidgetEventResult.NotInstalledUpdatedModel -> NoModelDownload(context)
            WidgetEventResult.EmptyModel -> NoActiveMessages(context)
            WidgetEventResult.Error -> LocationErrorWidget(context)
            WidgetEventResult.LocationNoPermissions -> LocationPermissionContent(context)
            WidgetEventResult.OutOfLimits -> OutOfLimitsContent(context)
            is WidgetEventResult.MapFile -> {
                val mapUri = widgetEventResult.file.takeIf { it.isNotEmpty() }
                    ?.let(Uri::parse)

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(
                            ColorProvider(
                                day = Color(0xFF101114),
                                night = Color(0xFF101114)
                            )
                        )
                        .cornerRadius(28.dp)
                        .clickable(
                            onClick = actionRunCallback<RefreshPlacesWidgetAction>()
                        )
                ) {
                    if (mapUri != null) {
                        Image(
                            provider = ImageProvider(mapUri),
                            contentDescription = "Mapa",
                            modifier = GlanceModifier.fillMaxSize()
                        )
                        Box(
                            modifier = GlanceModifier.fillMaxSize().padding(horizontal = 16.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Row {
                                UpdateTimer(timer)
                                Spacer(GlanceModifier.width(4.dp))
                                Text(style = TextStyle(
                                    fontSize = 12.sp
                                ),
                                    text = context.getString(
                                        R.string.ortapme
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun OutOfLimitsContent(context: Context) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    ColorProvider(
                        day = Color(0xFF101114),
                        night = Color(0xFF101114)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(
                        R.string.out_of_limits
                    ),
                    style = TextStyle(
                        color = ColorProvider(day = Color.LightGray, night = Color.LightGray),
                        fontSize = 14.sp
                    )
                )
            }
        }
    }

    @Composable
    fun NoModelDownload(context: Context) {
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
        if (launchIntent != null) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(
                        ColorProvider(
                            day = Color(0xFF101114),
                            night = Color(0xFF101114)
                        )
                    )
                    .clickable(
                        onClick = actionStartActivity(
                            launchIntent.apply {
                                putExtra("widget_action", "chat")
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = context.getString(
                            R.string.no_model_install
                        ),
                        style = TextStyle(
                            color = ColorProvider(
                                day = Color.LightGray,
                                night = Color.LightGray
                            ),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun NoActiveMessages(context: Context) {
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
        if (launchIntent != null) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(
                        ColorProvider(
                            day = Color(0xFF101114),
                            night = Color(0xFF101114)
                        )
                    )
                    .clickable(
                        onClick = actionStartActivity(
                            launchIntent.apply {
                                putExtra("widget_action", "chat")
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = context.getString(
                            R.string.no_messages_title
                        ),
                        style = TextStyle(
                            color = ColorProvider(day = Color.White, night = Color.White),
                            fontSize = 16.sp
                        )
                    )

                    Spacer(GlanceModifier.height(8.dp))

                    Text(
                        text = context.getString(
                            R.string.no_messages_description
                        ),
                        style = TextStyle(
                            color = ColorProvider(
                                day = Color.LightGray,
                                night = Color.LightGray
                            ),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun LocationPermissionContent(context: Context) {
        Column(
            modifier = GlanceModifier.background(
                ColorProvider(
                    day = Color.Black,
                    night = Color.Black
                )
            )
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                provider = ImageProvider(R.drawable.locationoff),
                contentDescription = "Ubicación desactivada",
                modifier = GlanceModifier.size(40.dp)
            )

            Spacer(
                modifier = GlanceModifier.height(12.dp)
            )

            Text(
                text = context.getString(
                    R.string.location_permission_required
                ),
                style = TextStyle(
                    color = ColorProvider(
                        day = Color.White,
                        night = Color.White
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(
                modifier = GlanceModifier.height(8.dp)
            )

            Text(
                text = context.getString(
                    R.string.location_permission_message
                ),
                style = TextStyle(
                    color = ColorProvider(
                        day = Color.LightGray,
                        night = Color.LightGray
                    ),
                    fontSize = 13.sp
                )
            )
            Row(
                GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(R.drawable.restart),
                    contentDescription = "Retry",
                    modifier = GlanceModifier.size(32.dp)
                        .clickable(
                            onClick = actionRunCallback<RefreshPlacesWidgetAction>()
                        )
                )
                Spacer(GlanceModifier.width(6.dp))
                Image(
                    provider = ImageProvider(R.drawable.settings),
                    contentDescription = "Ajustes",
                    modifier = GlanceModifier.size(32.dp).clickable(
                        actionStartActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                "package:${context.packageName}".toUri()
                            )
                        )
                    )
                )
            }

        }
    }

    @Composable
    fun LocationLoadingWidget() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    ColorProvider(
                        day = Color(0xFF101114),
                        night = Color(0xFF101114)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
            }
        }
    }

    @Composable
    fun LocationErrorWidget(context: Context) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    ColorProvider(
                        day = Color(0xFF101114),
                        night = Color(0xFF101114)
                    )
                )
                .clickable(
                    actionRunCallback<RefreshPlacesWidgetAction>()
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(
                        R.string.tap_to_start
                    ),
                    style = TextStyle(
                        color = ColorProvider(day = Color.LightGray, night = Color.LightGray),
                        fontSize = 14.sp
                    )
                )
            }
        }
    }

    @Composable
    private fun UpdateTimer(nextUpdate: Long) {

        val remoteViews = RemoteViews(
            LocalContext.current.packageName,
            R.layout.widget_update_timer
        )

        val remaining =
            (nextUpdate - System.currentTimeMillis())
                .coerceAtLeast(0L)

        val base =
            SystemClock.elapsedRealtime() + remaining

        remoteViews.setChronometer(
            R.id.widget_update_timer,
            base,
            null,
            true
        )

        remoteViews.setChronometerCountDown(
            R.id.widget_update_timer,
            true
        )

        AndroidRemoteViews(
            remoteViews = remoteViews,
            modifier = GlanceModifier
                .wrapContentWidth()
                .wrapContentHeight()
        )
    }
}

class RefreshPlacesWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val useCase: PlacesWidgetObserverUseCase by
        KoinJavaComponent.inject(
            PlacesWidgetObserverUseCase::class.java
        )

        useCase.refreshWidget()
    }
}