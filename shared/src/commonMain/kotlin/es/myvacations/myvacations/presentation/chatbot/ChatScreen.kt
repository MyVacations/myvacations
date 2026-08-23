package es.myvacations.myvacations.presentation.chatbot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import es.myvacations.myvacations.data.repository.LocationPermissionHandler
import es.myvacations.myvacations.domain.repository.LocationEventResult
import es.myvacations.myvacations.domain.repository.LocationEventResult.PermissionOk
import es.myvacations.myvacations.presentation.createedittrip.maxTextLength
import es.myvacations.myvacations.presentation.mapper.toLocalUserString
import kotlinx.coroutines.launch
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.buttonRetry
import myvacations.shared.generated.resources.close
import myvacations.shared.generated.resources.delete1chat
import myvacations.shared.generated.resources.deleteallchats
import myvacations.shared.generated.resources.examplePlace
import myvacations.shared.generated.resources.feedback
import myvacations.shared.generated.resources.gotomap
import myvacations.shared.generated.resources.icono
import myvacations.shared.generated.resources.onboarding_start
import myvacations.shared.generated.resources.placesTitle
import myvacations.shared.generated.resources.placesheader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen() {
    val viewModel = koinViewModel<ChatViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val stateLocation by viewModel.stateLocation.collectAsState()
    val state by viewModel.state.collectAsState()
    val dialogRequestingLocationPermissions =
        remember { mutableStateOf(stateLocation == LocationEventResult.PermissionDenied) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(stateLocation)
    {
        if (stateLocation == LocationEventResult.PermissionDenied) {
            dialogRequestingLocationPermissions.value = true
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResume()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LocationPermissionHandler(
        onUpdatePermission = {
            when (it) {
                is PermissionOk -> {
                    dialogRequestingLocationPermissions.value = false
                    viewModel.afterPermissionOk()
                }

                else -> dialogRequestingLocationPermissions.value = false
            }
        },
        dialogRequestingLocationPermissions = dialogRequestingLocationPermissions.value
    )

    if (stateLocation == PermissionOk)
        LocationChat(
            uiState,
            state,
            viewModel,
            updateDialogRequestingLocationPermissions = {
                dialogRequestingLocationPermissions.value = true
            },
            onTextChange = viewModel::onTextChatChange,
            openNavigationToPlace = viewModel::openNavigationToPlace,
            mapLocationCheck = viewModel::getMapLocation
        )
    else NoPermissionAvailable()
}

@Composable
fun ChatbotorTutorial(
    uiState: ChatUiState,
    viewModel: ChatViewModel,
    updateDialogRequestingLocationPermissions: () -> Unit,
    onTextChange: (String) -> Unit,
    openNavigationToPlace: (Double, Double, String?) -> Unit,
    mapLocationCheck: (ChatMessageUiState) -> Unit
) {
    if (uiState.tutorial) ChatBotScreenInfo(
        onContinue = viewModel::disableTutorial
    )
    else ChatBotScreen(
        uiState,
        onSearch = { fromUser, isRetry ->
            viewModel.sendASearch(fromUser, isRetry)
        },
        retryLocation = {
            viewModel.getMainLocation()
        },
        getNearestPlaces = viewModel::getNearestPlaces,
        openTutorial = viewModel::enableTutorial,
        messageList = uiState.messages,
        updateDialogRequestingLocationPermissions = updateDialogRequestingLocationPermissions,
        onTextChange = onTextChange,
        openNavigationToPlace = openNavigationToPlace,
        deleteAElement = viewModel::deleteAElement,
        deleteAll = viewModel::deleteAll,
        positiveFeedback = viewModel::positiveFeedback,
        negativeFeedback = viewModel::negativeFeedback,
        mapLocationCheck = mapLocationCheck
    )
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
        AiWelcomeCard(onContinue)
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
fun ChatBotScreen(
    uiState: ChatUiState,
    onSearch: (String, Boolean) -> Unit,
    retryLocation: () -> Unit,
    getNearestPlaces: (List<ElementsFoundUiState>) -> List<ElementsFoundUiState>,
    openTutorial: () -> Unit,
    messageList: List<ChatMessageUiState>,
    updateDialogRequestingLocationPermissions: () -> Unit,
    onTextChange: (String) -> Unit,
    openNavigationToPlace: (Double, Double, String?) -> Unit,
    deleteAElement: (Long) -> Unit,
    deleteAll: () -> Unit,
    positiveFeedback: (ChatMessageUiState) -> Unit,
    negativeFeedback: (ChatMessageUiState) -> Unit,
    mapLocationCheck: (ChatMessageUiState) -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(16.dp),
        topBar = {
            IconButton(onClick = openTutorial)
            {
                Icon(
                    Icons.AutoMirrored.Filled.Help,
                    null,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        bottomBar = {
            if (messageList.isNotEmpty()) ChatElement(
                uiState,
                sendText = onSearch,
                onTextChange = onTextChange
            )
        }
    )
    { paddingValues ->
        var elementDetail by remember {
            mutableStateOf<ElementsFoundUiState?>(null)
        }

        elementDetail?.let { place ->
            OpenPlaceDetails(
                place = place,
                onDismiss = {
                    elementDetail = null
                },
                onNavigate = openNavigationToPlace
            )
        }

        if (messageList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    stringResource(Res.string.placesTitle),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                ChatElement(
                    uiState,
                    sendText = onSearch,
                    onTextChange = onTextChange
                )
            }
        } else {
            val sortedMessages = remember(messageList) {
                messageList.sortedByDescending { it.time }
            }

            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { sortedMessages.size }
            )

            LaunchedEffect(sortedMessages.size) {
                if (
                    sortedMessages.isNotEmpty() &&
                    pagerState.currentPage >= sortedMessages.size
                ) {
                    pagerState.animateScrollToPage(
                        sortedMessages.lastIndex
                    )
                }
            }

            var showMenu by remember { mutableStateOf(false) }
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                ),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val selectedMessage =
                        sortedMessages.getOrNull(pagerState.currentPage)

                    if (selectedMessage != null) {
                        DropdownMenuScreen(
                            elementSelected = selectedMessage.id,
                            showMenu = showMenu,
                            updateShowMenuFalse = { showMenu = false },
                            deleteAElement = deleteAElement,
                            deleteAll = deleteAll
                        )
                    }
                    PageNumberIndicatorScreen(pagerState, sortedMessages)
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                            .combinedClickable(onClick = {}, onLongClick = {
                                showMenu = true
                            })
                    ) { page ->

                        val message = sortedMessages[page]

                        LaunchedEffect(
                            message.id,uiState.updatedLocation
                        ) {
                            if (message.bot != null) mapLocationCheck(message)
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize().padding(vertical = 12.dp).padding(start = 12.dp)
                        ) {
                            item {
                                val time = remember(message.time)
                                {
                                    message.time
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = time.toLocalUserString())
                                }
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item {
                                UserMessage(message.user.text)
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            if (!message.isItemLoading && message.bot != null) {
                                item {
                                    BotMessage(
                                        uiState,
                                        message,
                                        retry = {
                                            retryLocation()
                                            onSearch(
                                                message.user.text,
                                                message.bot.retryOn
                                            )
                                        },
                                        updateDialogRequestingLocationPermissions,
                                        itemSelected = {
                                            elementDetail = it
                                        }
                                    )
                                }

                                item {
                                    Spacer(Modifier.height(16.dp))
                                    if (!message.bot.retryOn) {
                                        Text(
                                            modifier = Modifier.padding(8.dp),
                                            text = stringResource(Res.string.placesheader)
                                        )
                                    }
                                }

                                items(
                                    getNearestPlaces(
                                        message.bot.elementsFound
                                    )
                                ) { place ->
                                    ItemPlace(uiState, place, openPlaceDetails = {
                                        elementDetail = it
                                    })
                                }
                                if (!message.feedback.feedbackDone) {
                                    item {
                                        Feedback(
                                            message,
                                            positiveFeedback = positiveFeedback,
                                            negativeFeedback = negativeFeedback
                                        )
                                    }
                                    item { Spacer(Modifier.height(62.dp)) }
                                } else {
                                    item { Spacer(Modifier.height(62.dp)) }
                                }
                            } else {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Feedback(
    message: ChatMessageUiState,
    positiveFeedback: (ChatMessageUiState) -> Unit,
    negativeFeedback: (ChatMessageUiState) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(stringResource(Res.string.feedback))
        Spacer(Modifier.width(12.dp))
        IconButton(onClick = {
            positiveFeedback(message)
        })
        {
            Icon(
                Icons.Default.ThumbUp,
                null,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        IconButton(onClick = {
            negativeFeedback(message)
        })
        {
            Icon(
                Icons.Default.ThumbDown,
                null,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun DropdownMenuScreen(
    elementSelected: Long,
    showMenu: Boolean,
    updateShowMenuFalse: () -> Unit,
    deleteAElement: (Long) -> Unit,
    deleteAll: () -> Unit
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = {
            updateShowMenuFalse()
        }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.delete1chat)) },
            onClick = {
                updateShowMenuFalse()
                deleteAElement(elementSelected)
            }
        )

        DropdownMenuItem(
            text = { Text(stringResource(Res.string.deleteallchats)) },
            onClick = {
                updateShowMenuFalse()
                deleteAll()
            }
        )
    }
}

@Composable
fun OpenPlaceDetails(
    place: ElementsFoundUiState,
    onDismiss: () -> Unit,
    onNavigate: (Double, Double, String?) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconForPlaceType(place.type ?: ""),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = place.name ?: "Lugar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )

                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // Distancia
                place.distance?.let {
                    PlaceDetailRow(
                        icon = Icons.Default.NearMe,
                        title = "Distancia",
                        value = formatDistance(it)
                    )
                }

                // Dirección
                place.address?.takeIf { it.isNotBlank() }?.let {
                    PlaceDetailRow(
                        icon = Icons.Default.LocationOn,
                        title = "Dirección",
                        value = it
                    )
                }

                // Teléfono
                place.phone?.takeIf { it.isNotBlank() }?.let {
                    PlaceDetailRow(
                        icon = Icons.Default.Phone,
                        title = "Teléfono",
                        value = it
                    )
                }

                // Web
                place.website?.takeIf { it.isNotBlank() }?.let {
                    PlaceDetailRow(
                        icon = Icons.Default.Language,
                        title = "Web",
                        value = it
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        modifier = Modifier.weight(0.8f),
                        onClick = onDismiss
                    ) {
                        Text(stringResource(Res.string.close))
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onNavigate(place.latitude, place.longitude, place.name)
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(Res.string.gotomap))
                            Spacer(Modifier.width(2.dp))
                            Icon(
                                modifier = Modifier.size(26.dp),
                                imageVector = Icons.Default.Directions,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceDetailRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.Top
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge, fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun PageNumberIndicatorScreen(
    pagerState: PagerState,
    sortedMessages: List<ChatMessageUiState>
) {
    val maxIndicators = 7
    val currentPage = pagerState.currentPage
    val totalPages = sortedMessages.size
    val scope = rememberCoroutineScope()
    val startPage = when {
        totalPages <= maxIndicators -> 0
        currentPage <= maxIndicators / 2 -> 0
        currentPage >= totalPages - maxIndicators / 2 ->
            totalPages - maxIndicators

        else ->
            currentPage - maxIndicators / 2
    }

    val endPage = minOf(
        startPage + maxIndicators,
        totalPages
    )

    Row(
        modifier = Modifier
            .height(32.dp)
            .fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (index in startPage until endPage) {

            val isSelected = index == currentPage

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(
                        if (isSelected) 9.dp else 6.dp
                    )
                    .clip(CircleShape)
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
            )
        }
    }
}

@Composable
fun BotMessage(
    uiState: ChatUiState,
    chatMessage: ChatMessageUiState,
    retry: () -> Unit,
    updateDialogRequestingLocationPermissions: () -> Unit,
    itemSelected: (ElementsFoundUiState) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            ),
            shape = CircleShape,
            color = Color(0xFF1F2332),
        ) {
            Icon(
                modifier = Modifier.border(
                    BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ).padding(8.dp),
                painter = painterResource(Res.drawable.icono),
                contentDescription = "ProfileBotIcon",
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text("MyVacations Bot")
    }
    Spacer(modifier = Modifier.height(8.dp))
    Surface(
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = chatMessage.bot?.text ?: ""
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (chatMessage.bot?.text?.isEmpty() == true) {
                MapScreen(
                    uiState,
                    chatMessage,
                    updateDialogRequestingLocationPermissions,
                    itemSelected
                )
            }
            if (chatMessage.bot?.retryOn == true) {
                Button(onClick = retry)
                {
                    Text(stringResource(Res.string.buttonRetry))
                }
            }
        }
    }
}


@Composable
fun UserMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 4.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
                text = message
            )
        }
    }
}

@Composable
private fun ChatElement(
    uiState: ChatUiState,
    sendText: (String, Boolean) -> Unit,
    onTextChange: (String) -> Unit
) {
    Column {
        val error = remember { mutableStateOf(false) }
        Row {
            Spacer(Modifier.weight(1f))
            Text(
                text = "${uiState.chatText.length}/30",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().background(
                MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ),
            value = uiState.chatText,
            onValueChange = { newValue ->
                val filtered = newValue.filter {
                    it.isLetter() || it.isWhitespace()
                }

                if (filtered.length <= maxTextLength) {
                    onTextChange(filtered)
                }
            },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            trailingIcon = {
                IconButton(onClick = {
                    val message = uiState.chatText.trim()
                    if (message.isNotEmpty() && message.length <= 30) {
                        sendText(message, false)
                        onTextChange("")
                        error.value = false
                    } else error.value = true
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
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            placeholder = {

                if (uiState.chatText.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.examplePlace),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}