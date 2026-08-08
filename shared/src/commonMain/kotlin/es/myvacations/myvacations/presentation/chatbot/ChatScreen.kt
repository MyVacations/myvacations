package es.myvacations.myvacations.presentation.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import myvacations.shared.generated.resources.onboarding_start
import myvacations.shared.generated.resources.retry
import myvacations.shared.generated.resources.update_model
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen() {
    val viewModel = koinViewModel<ChatViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val state by viewModel.state.collectAsState()
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is ModelState.ResumeInstallation -> {

                    val element = state as ModelState.ResumeInstallation

                    Text(
                        when (element.stage) {
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
                                progress = { element.progress },
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onError
                            )

                            Spacer(Modifier.width(12.dp))

                            Text("${(element.progress * 100).toInt()}%")
                        }

                        if (element.totalForDownload != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${element.downloaded} MB / ${element.totalForDownload} MB"
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row{
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
                    val version = (state as ModelState.NeedUpdate)
                    Text(stringResource(Res.string.new_version_available,version.versionAvailable))
                    Button(
                        onClick = {
                            viewModel.installUpdateModel()
                        }
                    ) {
                        Text(stringResource(Res.string.update_model))
                    }
                }

                is ModelState.Installing -> {
                    val element = (state as ModelState.Installing)
                    Text(
                        when (element.stage) {
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
                                progress = { element.progress },
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = "${(element.progress * 100).toInt()}%"
                            )
                        }
                        if (element.totalForDownload != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${element.downloaded} MB / ${element.totalForDownload} MB"
                                )
                            }
                        }
                    }
                }

                is ModelState.WaitingConnection -> {
                    val element = (state as ModelState.WaitingConnection)
                    Column {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = { element.progress },
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onError
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = "${(element.progress * 100).toInt()}%"
                            )
                        }
                        if (element.totalForDownload != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${element.downloaded} MB / ${element.totalForDownload} MB"
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
                    var textToIa by rememberSaveable { mutableStateOf("") }
                    if (uiState.tutorial) ChatBotScreenInfo(
                        onContinue = viewModel::disableTutorial
                    )
                    else ChatBotScreen(
                        text = textToIa,
                        onTextChange = { textToIa = it },
                        onSearch = {
                            viewModel.sendASearch(textToIa)
                        },
                        openTutorial = viewModel::enableTutorial,
                        messageList = uiState.messages
                    )
                }
            }
        }
    }
}


@Composable
fun ChatBotScreenInfo(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AiWelcomeCard()
        Spacer(Modifier.height(6.dp))
        HorizontalDivider(modifier = Modifier.height(2.dp))
        Spacer(Modifier.height(6.dp))
        Button(
            onClick = onContinue
        ) {
            Text(stringResource(Res.string.onboarding_start))
        }
    }
}


@Composable
fun AiWelcomeCard() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Descubre lugares con IA",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Describe con tus propias palabras el lugar que buscas y la IA identificará la categoría más adecuada para mostrarte lugares cercanos.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ejemplos",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Text(
                                text = "Quiero desayunar",
                                modifier = Modifier.padding(16.dp), fontSize = 9.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Icon(
                            Icons.Default.ArrowDownward,
                            null
                        )

                        Spacer(Modifier.height(12.dp))
                        SuggestionChip(
                            onClick = {},
                            enabled = false,
                            label = {
                                Text(
                                    text = "☕ CAFÉ",
                                    fontSize = 9.sp
                                )
                            }
                        )
                    }

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Text(
                                text = "Restaurante de lujo",
                                modifier = Modifier.padding(16.dp), fontSize = 9.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Icon(
                            Icons.Default.ArrowDownward,
                            null
                        )

                        Spacer(Modifier.height(12.dp))
                        SuggestionChip(
                            onClick = {},
                            enabled = false,
                            label = {
                                Text(
                                    text = "\uD83C\uDF7D RESTAURANTE",
                                    fontSize = 9.sp
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBotScreen(
    text: String,
    onTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    openTutorial: () -> Unit,
    messageList: List<ChatMessageUiState>
) {
    Scaffold(
        modifier = Modifier.padding(16.dp),
        topBar = {
            Text(modifier = Modifier.clickable(onClick = {
                openTutorial()
            }), text = "Abrir tutorial")
        },
        bottomBar = {
            if (messageList.isNotEmpty()) ChatElement(
                text,
                onTextChange,
                sendText = onSearch
            )
        },
    )
    { paddingValues ->
        if (messageList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    "¿Qué lugar buscas?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                ChatElement(text, onTextChange, sendText = onSearch)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(messageList)
                {
                    Text(it.text)
                }
            }
        }
    }
}

@Composable
private fun ExampleChip(
    text: String
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ChatElement(text: String, onTextChange: (String) -> Unit, sendText: () -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = Modifier.fillMaxWidth(),
        minLines = 1,
        maxLines = 3,
        trailingIcon = {
            IconButton(onClick = {
                sendText()
            })
            {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = color,
                            shape = CircleShape
                        ), contentAlignment = Alignment.Center
                )
                {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowRight,
                        null,
                        modifier = Modifier.size(28.dp)
                    )
                }

            }
        },
        placeholder = {
            Text("Ej. Quiero un sitio para desayunar")
        }
    )
}