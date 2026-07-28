package es.myvacations.myvacations.presentation.onboarding

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.onboarding_budget_description
import myvacations.shared.generated.resources.onboarding_budget_title
import myvacations.shared.generated.resources.onboarding_control_description
import myvacations.shared.generated.resources.onboarding_control_title
import myvacations.shared.generated.resources.onboarding_next
import myvacations.shared.generated.resources.onboarding_start
import myvacations.shared.generated.resources.onboarding_trips_description
import myvacations.shared.generated.resources.onboarding_trips_title
import myvacations.shared.generated.resources.onboarding_widget_description
import myvacations.shared.generated.resources.onboarding_widget_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


enum class OnboardingType {
    TRIPS,
    BUDGET,
    CONTROL,
    WIDGET
}

data class OnboardingPage(
    val title: String = "",
    val description: String = "",
    val type: OnboardingType
)

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel = koinViewModel(), onFinished: () -> Unit) {
    val onboardingPages = listOf(
        OnboardingPage(
            title = stringResource(Res.string.onboarding_trips_title),
            description = stringResource(Res.string.onboarding_trips_description),
            type = OnboardingType.TRIPS
        ),
        OnboardingPage(
            title = stringResource(Res.string.onboarding_budget_title),
            description = stringResource(Res.string.onboarding_budget_description),
            type = OnboardingType.BUDGET
        ),
        OnboardingPage(
            title = stringResource(Res.string.onboarding_control_title),
            description = stringResource(Res.string.onboarding_control_description),
            type = OnboardingType.CONTROL
        ),
        OnboardingPage(
            title = stringResource(Res.string.onboarding_widget_title),
            description = stringResource(Res.string.onboarding_widget_description),
            type = OnboardingType.WIDGET
        )
    )

    val pagerState = rememberPagerState(
        pageCount = { onboardingPages.size }
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            val item = onboardingPages[page]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OnboardingAnimation(
                    type = item.type
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(onboardingPages.size) { index ->

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        Button(
            onClick = {
                if (pagerState.currentPage < onboardingPages.lastIndex) {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }
                } else {
                    viewModel.onOnboardingFinished()
                    onFinished()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == onboardingPages.lastIndex) {
                    stringResource(Res.string.onboarding_start)
                } else {
                    stringResource(Res.string.onboarding_next)
                }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun OnboardingAnimation(
    type: OnboardingType
) {
    when (type) {
        OnboardingType.TRIPS -> TripsAnimation()
        OnboardingType.BUDGET -> BudgetAnimation()
        OnboardingType.CONTROL -> ControlAnimation()
        OnboardingType.WIDGET -> WidgetAnimation()
    }
}