package es.myvacations.myvacations.presentation.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun TripsAnimation() {
    var startAnimation by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val suitcaseOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 60.dp,
        animationSpec = tween(
            durationMillis = 700,
            easing = FastOutSlowInEasing
        )
    )

    val suitcaseAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(500)
    )

    val pinOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else (-50).dp,
        animationSpec = tween(
            durationMillis = 900,
            delayMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    val pinAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = 400
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Default.Map,
            contentDescription = null,
            modifier = Modifier.size(160.dp),
            tint = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.25f
            )
        )

        Icon(
            imageVector = Icons.Default.Luggage,
            contentDescription = null,
            modifier = Modifier
                .offset(
                    x = (-35).dp,
                    y = suitcaseOffset + 25.dp
                )
                .graphicsLayer {
                    alpha = suitcaseAlpha
                }
                .size(90.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier
                .offset(
                    x = 55.dp,
                    y = pinOffset
                )
                .graphicsLayer {
                    alpha = pinAlpha
                }
                .size(52.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}