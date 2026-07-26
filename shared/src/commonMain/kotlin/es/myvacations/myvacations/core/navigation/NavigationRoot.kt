package es.myvacations.myvacations.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import es.myvacations.myvacations.presentation.createedittrip.AddEditTripScreen
import es.myvacations.myvacations.presentation.dashboard.DashboardScreen
import es.myvacations.myvacations.presentation.notifications.ShowNotificationsScreen
import es.myvacations.myvacations.presentation.privacyandfaq.HelpSupportScreen
import es.myvacations.myvacations.presentation.privacyandfaq.PolicyScreen
import es.myvacations.myvacations.presentation.settings.SettingsScreen
import es.myvacations.myvacations.presentation.splash.SplashScreen
import es.myvacations.myvacations.presentation.statistics.StatisticsScreen
import es.myvacations.myvacations.presentation.tripdetail.TripDetailScreen
import es.myvacations.myvacations.presentation.trips.TripsScreen
import kotlinx.coroutines.launch
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.dashboard
import myvacations.shared.generated.resources.settings
import myvacations.shared.generated.resources.statistics
import myvacations.shared.generated.resources.trips
import org.jetbrains.compose.resources.stringResource

@Preview(showBackground = true)
@Composable
fun NavigationRoot(
    isLandscape: Boolean = false,
    tripIdFromWidget: String = "",
    widgetAction: String = ""
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    val navigationState = rememberSaveable(
        saver = NavigationStateSaver
    ) {
        NavigationState()
    }

    LaunchedEffect(
        tripIdFromWidget,
        widgetAction
    ) {
        if (
            navigationState.currentScreen != ScreenDestination.Splash &&
            tripIdFromWidget.isNotBlank() &&
            widgetAction.isNotBlank()
        ) {
            loadWidgetScreenNav(
                navigationState,
                tripIdFromWidget,
                widgetAction
            )
        }
    }

    with(navigationState) {
        Scaffold(
            bottomBar = {
                if (navigationState.currentScreen.showBottomBarUi) BottomBarUi(this)
            },
            floatingActionButton = {
                if (navigationState.currentScreen.showFloatingButton) {
                    FloatingActionButton(
                        containerColor = Color(0xFF00A884),
                        onClick = {
                            navigate(ScreenDestination.AddEdit())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Trip"
                        )
                    }
                }
            }, snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                )
            }

        ) { paddingValues ->
            Surface(
                modifier = Modifier.padding(paddingValues)
            ) {
                when (currentScreen) {
                    ScreenDestination.Splash -> SplashScreen(onFinished = {
                        loadWidgetScreenNav(navigationState, tripIdFromWidget, widgetAction)
                    })

                    ScreenDestination.Dashboard -> DashboardScreen(
                        onNotificationsClick = {
                            navigate(ScreenDestination.ShowNotifications)
                        },
                        onEditTripClick = {
                            navigate(ScreenDestination.TripDetail(it))
                        },
                        onStatisticsClick = {
                            navigate(ScreenDestination.Statistics)
                        })

                    ScreenDestination.Trips -> TripsScreen(openTripDetail = {
                        navigate(ScreenDestination.TripDetail(it))
                    })

                    ScreenDestination.Statistics -> StatisticsScreen()
                    ScreenDestination.Settings -> SettingsScreen(
                        onPolicyScreen = {
                            navigate(ScreenDestination.ShowPrivacyPolitic)
                        },
                        onHelpAndSupport = {
                            navigate(ScreenDestination.ShowHelpAndSupport)
                        })

                    ScreenDestination.ShowPrivacyPolitic -> {
                        PolicyScreen(onDismiss = {
                            popBackStack()
                        })
                    }

                    ScreenDestination.ShowHelpAndSupport -> {
                        HelpSupportScreen(onDismiss = {
                            popBackStack()
                        })
                    }

                    is ScreenDestination.ShowNotifications -> {
                        ShowNotificationsScreen(
                            onDismiss = {
                                popBackStack()
                            },
                            onClick = {
                                navigate(ScreenDestination.TripDetail(it))
                            })
                    }

                    is ScreenDestination.AddEdit -> {
                        val tripid = (currentScreen as ScreenDestination.AddEdit).tripId
                        val selectedExpenseFromWidget = (currentScreen as ScreenDestination.AddEdit).selectedExpenseFromWidget

                        AddEditTripScreen(tripid,selectedExpenseFromWidget, onDismiss = {
                            popBackStack()
                        })
                    }

                    is ScreenDestination.TripDetail -> {
                        val tripid = (currentScreen as ScreenDestination.TripDetail).tripId
                        TripDetailScreen(
                            tripId = tripid, onDismiss = {
                                popBackStack()
                            }, onEditTripClick = {
                                navigate(ScreenDestination.AddEdit(tripid))
                            }, onShowSnackbar = { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            })
                    }
                }
            }
        }

    }
}

fun loadWidgetScreenNav(
    navigationState: NavigationState,
    tripIdFromWidget: String,
    widgetAction: String
) {
    when (widgetAction) {
        "add_expense" if tripIdFromWidget.isNotBlank() -> {
            if (tripIdFromWidget.isBlank()) return navigationState.navigate(
                ScreenDestination.Dashboard
            )

            navigationState.navigate(ScreenDestination.Dashboard)

            navigationState.navigate(
                ScreenDestination.TripDetail(tripIdFromWidget)
            )

            navigationState.navigate(
                ScreenDestination.AddEdit(tripIdFromWidget, true)
            )
        }
        "trip_detail" if tripIdFromWidget.isNotBlank() -> {
            if (tripIdFromWidget.isBlank()) return navigationState.navigate(
                ScreenDestination.Dashboard
            )
            navigationState.navigate(ScreenDestination.Dashboard)
            navigationState.navigate(
                ScreenDestination.TripDetail(
                    tripId = tripIdFromWidget
                )
            )
        }
        else -> {
            navigationState.navigate(ScreenDestination.Dashboard)
        }
    }
}

@Composable
private fun BottomBarUi(state: NavigationState = NavigationState()) {
    with(state) {
        NavigationBar {
            NavigationBarItem(
                selected = currentScreen == ScreenDestination.Dashboard,
                onClick = {
                    navigateBottomBar(ScreenDestination.Dashboard)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(Res.string.dashboard)) }
            )
            NavigationBarItem(
                selected = currentScreen == ScreenDestination.Trips,
                onClick = {
                    navigateBottomBar(ScreenDestination.Trips)
                },
                icon = { Icon(imageVector = Icons.Default.Map, contentDescription = null) },
                label = { Text(stringResource(Res.string.trips)) }
            )
            NavigationBarItem(
                selected = currentScreen == ScreenDestination.Statistics,
                onClick = {
                    navigateBottomBar(ScreenDestination.Statistics)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(Res.string.statistics)) }
            )
            NavigationBarItem(
                selected = currentScreen == ScreenDestination.Settings,
                onClick = {
                    navigateBottomBar(ScreenDestination.Settings)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(Res.string.settings)) }
            )
        }
    }
}