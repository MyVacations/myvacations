package es.myvacations.myvacations.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
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
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import es.myvacations.myvacations.data.repository.ActiveErrorLocationKey
import es.myvacations.myvacations.data.repository.ActiveLoadingLocationKey
import es.myvacations.myvacations.data.repository.ActiveLocationPermissionsKey
import es.myvacations.myvacations.data.repository.ActiveMapFileKey
import es.myvacations.myvacations.data.repository.ActiveNoMessagesKey
import es.myvacations.myvacations.data.repository.ActiveNoModelKey
import es.myvacations.myvacations.data.repository.LocationForegroundService
import es.myvacations.myvacations.domain.usecase.eventsusecase.PlacesWidgetObserverUseCase
import es.myvacations.myvacations.shared.R
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

            val permission = preferences[ActiveLocationPermissionsKey] ?: true
            val loading = preferences[ActiveLoadingLocationKey] ?: false
            val error = preferences[ActiveErrorLocationKey] ?: false
            val nomessages = preferences[ActiveNoMessagesKey] ?: false
            val nomodel = preferences[ActiveNoModelKey] ?: false

            val mapUri =
                preferences[ActiveMapFileKey]
                    ?.takeIf { it.isNotEmpty() }
                    ?.let(Uri::parse)

            WidgetContent(
                context,
                permission,
                mapUri,
                loading,
                error,
                nomessages,
                nomodel
            )
        }
    }

    @Composable
    private fun WidgetContent(
        context: Context,
        permission: Boolean,
        mapUri: Uri?,
        loading: Boolean,
        error: Boolean,
        nomessages: Boolean,
        nomodel: Boolean,
    ) {
        when {
            loading -> LocationLoadingWidget()
            nomodel -> NoModelDownload(context)
            nomessages -> NoActiveMessages(context)
            error -> LocationErrorWidget(context)
            !permission -> LocationPermissionContent(context)
            else -> {
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
                    }
                }
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
                            color = ColorProvider(day = Color.LightGray, night = Color.LightGray),
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
                            color = ColorProvider(day = Color.LightGray, night = Color.LightGray),
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
                    onClick = actionRunCallback<RefreshPlacesWidgetAction>()
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(
                        R.string.location_error
                    ),
                    style = TextStyle(
                        color = ColorProvider(day = Color.White, night = Color.White),
                        fontSize = 16.sp
                    )
                )

                Spacer(GlanceModifier.height(8.dp))

                Text(
                    text = context.getString(
                        R.string.location_retry
                    ),
                    style = TextStyle(
                        color = ColorProvider(day = Color.LightGray, night = Color.LightGray),
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

class RefreshPlacesWidgetAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // 1. Refresco inmediato
        val useCase: PlacesWidgetObserverUseCase by
        KoinJavaComponent.inject(
            PlacesWidgetObserverUseCase::class.java
        )

        useCase.refreshWidget()

        // 2. Arrancar GPS continuo
        val serviceIntent = Intent(
            context,
            LocationForegroundService::class.java
        )

        ContextCompat.startForegroundService(
            context,
            serviceIntent
        )
    }
}