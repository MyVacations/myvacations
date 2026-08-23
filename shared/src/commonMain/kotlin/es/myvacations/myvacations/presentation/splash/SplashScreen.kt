package es.myvacations.myvacations.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.logo_background
import myvacations.shared.generated.resources.logo_plane
import org.jetbrains.compose.resources.painterResource

@Preview(showBackground = true)
@Composable
fun SplashScreen(
    onFinished: () -> Unit = {}
) {
    val progress = remember {
        Animatable(0f)
    }

    val trail = remember {
        mutableStateListOf<Offset>()
    }
    val blurTrail = remember {
        mutableStateListOf<Offset>()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2332)),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(Res.drawable.logo_background),
            modifier = Modifier.size(280.dp),
            contentDescription = null
        )
        val position = Offset(
            x = lerp(-180f, 0f, progress.value),
            y = lerp(100f, 0f, progress.value)
        )
        val planeAlpha = when {
            progress.value <= 2f -> 1f
            progress.value >= 4f -> 0f
            else -> 3f - progress.value
        }.coerceIn(0f, 1f)
        blurTrail.forEachIndexed { index, oldPosition ->

            Image(
                painter = painterResource(Res.drawable.logo_plane),
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer {
                        translationX = oldPosition.x
                        translationY = oldPosition.y
                        alpha = (index.toFloat() / trail.size * 0.25f) * planeAlpha
                    },
                contentDescription = null
            )
        }

        PaintCanvas(Modifier.matchParentSize(), trail, planeAlpha)

        Image(
            painter = painterResource(Res.drawable.logo_plane),
            modifier = Modifier
                .size(180.dp).graphicsLayer {
                    translationX = position.x
                    translationY = position.y
                    alpha = planeAlpha
                },
            contentDescription = null
        )
        LaunchedEffect(position)
        {
            trail.add(position)
            blurTrail.add(position)
            if (blurTrail.size > 10) {
                blurTrail.removeAt(0)
            }
            if (trail.size > 50) {
                trail.removeAt(0)
            }
        }
    }

    LaunchedEffect(Unit) {

        progress.animateTo(
            targetValue = 4f,
            animationSpec = tween(2000)
        )
        onFinished()
    }
}

@Composable
fun PaintCanvas(
    modifier: Modifier,
    trail: SnapshotStateList<Offset>,
    planeAlpha: Float
) {
    Canvas(
        modifier = modifier
    ) {
        val path = Path()

        if (trail.isNotEmpty()) {
            path.moveTo(
                center.x + trail.first().x - 170f,
                center.y + trail.first().y + 70f
            )
            trail.drop(1).forEach { _ ->

                for (i in 1 until trail.size) {

                    val previous = trail[i - 1]
                    val current = trail[i]

                    val midX = (previous.x + current.x) / 2f
                    val midY = (previous.y + current.y) / 2f

                    path.quadraticTo(
                        center.x + previous.x - 170f,
                        center.y + previous.y + 70f,
                        center.x + midX - 170f,
                        center.y + midY + 70f
                    )
                }
            }
            for (i in 1 until trail.size) {

                val start = Offset(
                    center.x + trail[i - 1].x - 170f,
                    center.y + trail[i - 1].y + 70f
                )

                val end = Offset(
                    center.x + trail[i].x - 170f,
                    center.y + trail[i].y + 70f
                )

                val factor = i.toFloat() / trail.size
                val alpha = (factor * factor * factor * 0.8f) * planeAlpha
                drawLine(
                    color = Color.White.copy(alpha = alpha * 0.15f),
                    start = start,
                    end = end,
                    strokeWidth = 18f * factor * factor,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White.copy(alpha = alpha),
                    start = start,
                    end = end,
                    strokeWidth = 1f + factor * 7f,
                    cap = StrokeCap.Round
                )

            }
        }
    }
}

