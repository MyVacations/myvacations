package es.myvacations.myvacations.presentation.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.onboarding_addexpense
import myvacations.shared.generated.resources.onboarding_budgetrest
import myvacations.shared.generated.resources.onboarding_extraexpense
import myvacations.shared.generated.resources.onboarding_rest
import myvacations.shared.generated.resources.onboarding_travel
import org.jetbrains.compose.resources.stringResource

@Composable
fun WidgetAnimation() {
    var startAnimation by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val widgetScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )

    val widgetAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(500)
    )

    val progress by animateFloatAsState(
        targetValue = if (startAnimation) 0.6f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
    BoxWithConstraints( modifier = Modifier
        .fillMaxWidth(),
        contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .graphicsLayer {
                    scaleX = widgetScale
                    scaleY = widgetScale
                    alpha = widgetAlpha
                },
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF111719)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Almuñecar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "España",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    Text(
                        text = stringResource(Res.string.onboarding_rest),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 8.dp,
                            trackColor = Color(0xFF454545),
                            color = Color(0xFF11AC1F)
                        )

                        Text(
                            text = "60%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "30€",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text(
                                text = "/ 50€",
                                fontSize = 12.sp,
                                color = Color(0xFFE0E0E0)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text(
                                text = stringResource(Res.string.onboarding_extraexpense),
                                fontSize = 10.sp,
                                color = Color(0xFFE0E0E0)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )
                        Text(
                            text = stringResource(Res.string.onboarding_budgetrest),
                            fontSize = 11.sp,
                            color = Color(0xFF11AC1F)
                        )

                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(50)),
                            color = Color(0xFF11AC1F),
                            trackColor = Color(0xFF30254D)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006773),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.onboarding_addexpense),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    TextButton(
                        onClick = {}
                    ) {
                        Text(
                            text = stringResource(Res.string.onboarding_travel),
                            color = Color(0xFFE0E0E0)
                        )
                    }
                }
            }
        }
    }
}