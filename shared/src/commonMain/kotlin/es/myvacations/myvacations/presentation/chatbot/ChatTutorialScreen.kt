package es.myvacations.myvacations.presentation.chatbot

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.ai_tutorial_abades_loja
import myvacations.shared.generated.resources.ai_tutorial_address
import myvacations.shared.generated.resources.ai_tutorial_back
import myvacations.shared.generated.resources.ai_tutorial_bar
import myvacations.shared.generated.resources.ai_tutorial_bar_andalucia
import myvacations.shared.generated.resources.ai_tutorial_continue
import myvacations.shared.generated.resources.ai_tutorial_details_description
import myvacations.shared.generated.resources.ai_tutorial_details_title
import myvacations.shared.generated.resources.ai_tutorial_distance_1_1km
import myvacations.shared.generated.resources.ai_tutorial_distance_350m
import myvacations.shared.generated.resources.ai_tutorial_distance_720m
import myvacations.shared.generated.resources.ai_tutorial_example_coffee
import myvacations.shared.generated.resources.ai_tutorial_example_hungry
import myvacations.shared.generated.resources.ai_tutorial_example_japanese
import myvacations.shared.generated.resources.ai_tutorial_example_pizza
import myvacations.shared.generated.resources.ai_tutorial_examples_description
import myvacations.shared.generated.resources.ai_tutorial_examples_title
import myvacations.shared.generated.resources.ai_tutorial_get_directions
import myvacations.shared.generated.resources.ai_tutorial_intro_bot_message
import myvacations.shared.generated.resources.ai_tutorial_intro_description
import myvacations.shared.generated.resources.ai_tutorial_intro_title
import myvacations.shared.generated.resources.ai_tutorial_intro_user_message
import myvacations.shared.generated.resources.ai_tutorial_map_description
import myvacations.shared.generated.resources.ai_tutorial_map_title
import myvacations.shared.generated.resources.ai_tutorial_navigation_description
import myvacations.shared.generated.resources.ai_tutorial_navigation_title
import myvacations.shared.generated.resources.ai_tutorial_phone
import myvacations.shared.generated.resources.ai_tutorial_regional
import myvacations.shared.generated.resources.ai_tutorial_restaurant
import myvacations.shared.generated.resources.ai_tutorial_restaurant_flati
import myvacations.shared.generated.resources.ai_tutorial_results_description
import myvacations.shared.generated.resources.ai_tutorial_results_title
import myvacations.shared.generated.resources.ai_tutorial_skip
import myvacations.shared.generated.resources.ai_tutorial_start
import myvacations.shared.generated.resources.ai_tutorial_website
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource


@Composable
fun AiWelcomeCard(
    onFinish: () -> Unit
) {
    var currentPage by remember {
        mutableIntStateOf(0)
    }

    var pages by remember {
        mutableStateOf<List<ChatTutorialPage>?>(null)
    }

    LaunchedEffect(Unit) {
        pages = listOf(
            ChatTutorialPage(
                title = getString(
                    Res.string.ai_tutorial_intro_title
                ),
                description = getString(
                    Res.string.ai_tutorial_intro_description
                ),
                type = ChatTutorialPageType.INTRO
            ),
            ChatTutorialPage(
                title = getString(
                    Res.string.ai_tutorial_examples_title
                ),
                description = getString(
                    Res.string.ai_tutorial_examples_description
                ),
                type = ChatTutorialPageType.EXAMPLES
            ),
            ChatTutorialPage(
                title = getString(
                    Res.string.ai_tutorial_results_title
                ),
                description = getString(
                    Res.string.ai_tutorial_results_description
                ),
                type = ChatTutorialPageType.RESULTS
            ),
            ChatTutorialPage(
                title = getString(
                    Res.string.ai_tutorial_map_title
                ),
                description = getString(
                    Res.string.ai_tutorial_map_description
                ),
                type = ChatTutorialPageType.MAP
            ),
            ChatTutorialPage(
                title = getString(
                    Res.string.ai_tutorial_details_title
                ),
                description = getString(
                    Res.string.ai_tutorial_details_description
                ),
                type = ChatTutorialPageType.DETAILS
            ),
            ChatTutorialPage(
                title = getString(
                    Res.string.ai_tutorial_navigation_title
                ),
                description = getString(
                    Res.string.ai_tutorial_navigation_description
                ),
                type = ChatTutorialPageType.NAVIGATION
            )
        )
    }

    if (pages == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    val tutorialPages = pages!!
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 20.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (currentPage < tutorialPages.lastIndex) {
                TextButton(
                    onClick = onFinish
                ) {
                    Text(
                        text = stringResource(
                            Res.string.ai_tutorial_skip
                        )
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (tutorialPages[currentPage].type) {

                ChatTutorialPageType.INTRO -> {
                    ChatTutorialIntro()
                }

                ChatTutorialPageType.EXAMPLES -> {
                    ChatTutorialExamples()
                }

                ChatTutorialPageType.RESULTS -> {
                    ChatTutorialResults()
                }

                ChatTutorialPageType.MAP -> {
                    ChatTutorialMap()
                }

                ChatTutorialPageType.DETAILS -> {
                    ChatTutorialDetails()
                }

                ChatTutorialPageType.NAVIGATION -> {
                    ChatTutorialNavigation()
                }
            }
        }

        Text(
            text = tutorialPages[currentPage].title,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = tutorialPages[currentPage].description,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tutorialPages.indices.forEach { index ->

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(
                            width = if (index == currentPage) {
                                24.dp
                            } else {
                                8.dp
                            },
                            height = 8.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (index == currentPage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (currentPage == tutorialPages.lastIndex) {

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onFinish
            ) {
                Text(
                    text = stringResource(
                        Res.string.ai_tutorial_start
                    )
                )
            }

        } else {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                if (currentPage > 0) {

                    TextButton(
                        onClick = {
                            currentPage--
                        }
                    ) {
                        Text(
                            text = stringResource(
                                Res.string.ai_tutorial_back
                            )
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                }

                Button(
                    onClick = {
                        currentPage++
                    }
                ) {
                    Text(
                        text = stringResource(
                            Res.string.ai_tutorial_continue
                        )
                    )
                }
            }
        }
    }
}


@Composable
private fun ChatTutorialIntro() {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        TutorialChatBubble(
            text = stringResource(
                Res.string.ai_tutorial_intro_user_message
            ),
            fromUser = true
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TutorialChatBubble(
            text = stringResource(
                Res.string.ai_tutorial_intro_bot_message
            ),
            fromUser = false
        )
    }
}


@Composable
private fun ChatTutorialExamples() {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TutorialExample(
            icon = Icons.Default.Restaurant,
            text = stringResource(
                Res.string.ai_tutorial_example_pizza
            )
        )

        TutorialExample(
            icon = Icons.Default.LocalCafe,
            text = stringResource(
                Res.string.ai_tutorial_example_coffee
            )
        )

        TutorialExample(
            icon = Icons.Default.Restaurant,
            text = stringResource(
                Res.string.ai_tutorial_example_japanese
            )
        )

        TutorialExample(
            icon = Icons.Default.Restaurant,
            text = stringResource(
                Res.string.ai_tutorial_example_hungry
            )
        )
    }
}


@Composable
private fun ChatTutorialResults() {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        TutorialMiniMap()

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        TutorialPlaceRow(
            number = 1,
            name = stringResource(
                Res.string.ai_tutorial_restaurant_flati
            ),
            type = stringResource(
                Res.string.ai_tutorial_restaurant
            ),
            distance = stringResource(
                Res.string.ai_tutorial_distance_350m
            )
        )

        TutorialPlaceRow(
            number = 2,
            name = stringResource(
                Res.string.ai_tutorial_abades_loja
            ),
            type = stringResource(
                Res.string.ai_tutorial_restaurant
            ),
            distance = stringResource(
                Res.string.ai_tutorial_distance_720m
            )
        )

        TutorialPlaceRow(
            number = 3,
            name = stringResource(
                Res.string.ai_tutorial_bar_andalucia
            ),
            type = stringResource(
                Res.string.ai_tutorial_bar
            ),
            distance = stringResource(
                Res.string.ai_tutorial_distance_1_1km
            )
        )
    }
}


@Composable
private fun ChatTutorialMap() {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        TutorialMapWithPlaces()

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(
                            Res.string.ai_tutorial_restaurant_flati
                        ),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(
                            Res.string.ai_tutorial_distance_350m
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}


@Composable
private fun ChatTutorialDetails() {
    TutorialPlaceDetailsCard()
}


@Composable
private fun ChatTutorialNavigation() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Directions,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = stringResource(
                    Res.string.ai_tutorial_restaurant_flati
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = stringResource(
                    Res.string.ai_tutorial_distance_350m
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                TutorialLocationPoint(
                    icon = Icons.Default.MyLocation
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                TutorialLocationPoint(
                    icon = Icons.Default.LocationOn
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = stringResource(
                    Res.string.ai_tutorial_navigation_description
                ),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun TutorialChatBubble(
    text: String,
    fromUser: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (fromUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(18.dp),
            color = if (fromUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ) {

            Text(
                text = text,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
private fun TutorialExample(
    icon: ImageVector,
    text: String
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@Composable
private fun TutorialMiniMap() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            )
    ) {

        TutorialMapMarker(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 55.dp,
                    top = 35.dp
                )
        )

        TutorialMapMarker(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = 70.dp,
                    top = 25.dp
                )
        )

        TutorialMapMarker(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 110.dp,
                    bottom = 25.dp
                )
        )

        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
private fun TutorialMapWithPlaces() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            )
    ) {

        TutorialMapMarker(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 45.dp,
                    top = 40.dp
                )
        )

        TutorialMapMarker(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = 55.dp,
                    top = 55.dp
                )
        )

        TutorialMapMarker(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 100.dp)
        )

        TutorialMapMarker(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 80.dp,
                    bottom = 40.dp
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
private fun TutorialMapMarker(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.primary
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
private fun TutorialPlaceRow(
    number: Int,
    name: String,
    type: String,
    distance: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = type,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = distance,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
private fun TutorialPlaceDetailsCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = stringResource(
                            Res.string.ai_tutorial_restaurant_flati
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${stringResource(Res.string.ai_tutorial_restaurant)} · ${
                            stringResource(Res.string.ai_tutorial_regional)
                        }",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            HorizontalDivider()

            TutorialDetailRow(
                icon = Icons.Default.LocationOn,
                text = stringResource(
                    Res.string.ai_tutorial_address
                )
            )

            TutorialDetailRow(
                icon = Icons.Default.NearMe,
                text = stringResource(
                    Res.string.ai_tutorial_distance_350m
                )
            )

            TutorialDetailRow(
                icon = Icons.Default.Phone,
                text = stringResource(
                    Res.string.ai_tutorial_phone
                )
            )

            TutorialDetailRow(
                icon = Icons.Default.Language,
                text = stringResource(
                    Res.string.ai_tutorial_website
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) {

                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = stringResource(
                        Res.string.ai_tutorial_get_directions
                    )
                )
            }
        }
    }
}


@Composable
private fun TutorialDetailRow(
    icon: ImageVector,
    text: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun TutorialLocationPoint(
    icon: ImageVector
) {

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.primaryContainer
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}