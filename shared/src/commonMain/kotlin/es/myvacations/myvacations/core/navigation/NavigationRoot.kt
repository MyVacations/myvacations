package es.myvacations.myvacations.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import es.myvacations.myvacations.presentation.createvacation.AddTripScreen
import es.myvacations.myvacations.presentation.dashboard.DashboardScreen
import es.myvacations.myvacations.presentation.settings.SettingsScreen
import es.myvacations.myvacations.presentation.statistics.StatisticsScreen
import es.myvacations.myvacations.presentation.trips.TripsScreen
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.dashboard
import myvacations.shared.generated.resources.settings
import myvacations.shared.generated.resources.statistics
import myvacations.shared.generated.resources.trips
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationRoot() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    var showDialog by remember {
        mutableStateOf(false)
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.Dashboard,
                    onClick = { currentScreen = Screen.Dashboard },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.dashboard)) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Trips,
                    onClick = { currentScreen = Screen.Trips },
                    icon = { Icon(imageVector = Icons.Default.Map, contentDescription = null) },
                    label = { Text(stringResource(Res.string.trips)) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Statistics,
                    onClick = { currentScreen = Screen.Statistics },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.statistics)) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Settings,
                    onClick = { currentScreen = Screen.Settings },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.settings)) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Trip"
                )
            }
        }
    ) { paddingValues ->
        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AddTripScreen(onDismiss = { showDialog = false })
                }
            }
        } else {
            Surface(
                modifier = Modifier.padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.Dashboard -> DashboardScreen()
                    Screen.Trips -> TripsScreen()
                    Screen.Statistics -> StatisticsScreen()
                    Screen.Settings -> SettingsScreen()
                }
            }
        }
    }
}

