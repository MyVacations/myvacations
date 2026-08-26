package es.myvacations.myvacations.presentation.chatbot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.domain.model.ModelInstallStage
import es.myvacations.myvacations.domain.model.ModelState
import es.myvacations.myvacations.domain.model.Stage
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.cancel
import myvacations.shared.generated.resources.completed
import myvacations.shared.generated.resources.continue_installation
import myvacations.shared.generated.resources.download_failed
import myvacations.shared.generated.resources.download_interrupted
import myvacations.shared.generated.resources.downloading_model
import myvacations.shared.generated.resources.extracting
import myvacations.shared.generated.resources.install_model
import myvacations.shared.generated.resources.installation_interrupted_extraction
import myvacations.shared.generated.resources.installation_interrupted_verification
import myvacations.shared.generated.resources.new_version_available
import myvacations.shared.generated.resources.notpermissionexplain
import myvacations.shared.generated.resources.retry
import myvacations.shared.generated.resources.update_model
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocationChat(
    uiState: ChatUiState,
    state: ModelState,
    viewModel: ChatViewModel,
    updateDialogRequestingLocationPermissions: () -> Unit,
    onTextChange: (String) -> Unit,
    openNavigationToPlace: (Double, Double, String?) -> Unit,
    mapLocationCheck: (message: ChatMessageUiState) -> Unit
) {
    if (uiState.isFullScreenLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DownloadStateMode(viewModel, state, uiState, updateDialogRequestingLocationPermissions,onTextChange,openNavigationToPlace,mapLocationCheck)
        }
    }
}

@Composable
fun DownloadStateMode(
    viewModel: ChatViewModel,
    state: ModelState,
    uiState: ChatUiState,
    updateDialogRequestingLocationPermissions: () -> Unit,
    onTextChange: (String) -> Unit,
    openNavigationToPlace: (Double, Double, String?) -> Unit,
    mapLocationCheck: (ChatMessageUiState) -> Unit
) {
    when (state) {
        is ModelState.ResumeInstallation -> {
            Text(
                when (state.stage) {
                    ModelInstallStage.DOWNLOADING -> stringResource(Res.string.download_interrupted)
                    ModelInstallStage.EXTRACTING -> stringResource(Res.string.installation_interrupted_extraction)
                    ModelInstallStage.VERIFYING -> stringResource(Res.string.installation_interrupted_verification)
                }
            )

            Spacer(Modifier.height(8.dp))
            Column {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onError
                    )

                    Spacer(Modifier.width(12.dp))

                    Text("${(state.progress * 100).toInt()}%")
                }

                if (state.totalForDownload != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${state.downloaded} MB / ${state.totalForDownload} MB"
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        viewModel.installUpdateModel()
                    }
                ) {
                    Text(stringResource(Res.string.continue_installation))
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        viewModel.cancelInstall()
                    }
                ) {
                    Text(stringResource(Res.string.cancel), color = Color.Red)
                }
            }

        }

        is ModelState.NotInstalled -> {
            Button(
                onClick = {
                    viewModel.installUpdateModel()
                }
            ) {
                Text(stringResource(Res.string.install_model))
            }
        }

        is ModelState.NeedUpdate -> {
            Text(stringResource(Res.string.new_version_available, state.versionAvailable))
            Button(
                onClick = {
                    viewModel.installUpdateModel()
                }
            ) {
                Text(stringResource(Res.string.update_model))
            }
        }

        is ModelState.Installing -> {
            Text(
                when (state.stage) {
                    Stage.DOWNLOADING -> stringResource(Res.string.downloading_model)
                    Stage.EXTRACTING -> stringResource(Res.string.extracting)
                    else -> {
                        stringResource(Res.string.completed)
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "${(state.progress * 100).toInt()}%"
                    )
                }
                if (state.totalForDownload != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${state.downloaded} MB / ${state.totalForDownload} MB"
                        )
                    }
                }
            }
        }

        is ModelState.WaitingConnection -> {
            Column {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onError
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "${(state.progress * 100).toInt()}%"
                    )
                }
                if (state.totalForDownload != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${state.downloaded} MB / ${state.totalForDownload} MB"
                        )
                    }
                }
            }
        }

        is ModelState.ConnectionTimeout -> {
            stringResource(Res.string.download_failed)

            Button(
                onClick = {
                    viewModel.installUpdateModel()
                }
            ) {
                stringResource(Res.string.retry)
            }
        }

        else -> {
            ChatbotorTutorial(uiState, viewModel, updateDialogRequestingLocationPermissions,onTextChange, openNavigationToPlace = openNavigationToPlace,mapLocationCheck)
        }
    }
}

@Composable
fun NoPermissionAvailable() {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LocationOff, "locationOff")
        Spacer(Modifier.width(12.dp))
        Text(stringResource(Res.string.notpermissionexplain))
    }
}