package es.myvacations.myvacations.presentation.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.login_continue_with
import myvacations.shared.generated.resources.login_email
import myvacations.shared.generated.resources.login_google
import myvacations.shared.generated.resources.login_register_later
import myvacations.shared.generated.resources.login_registration_optional
import myvacations.shared.generated.resources.login_skip
import myvacations.shared.generated.resources.login_subtitle
import myvacations.shared.generated.resources.login_welcome
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun LoginScreen(
    viewmodel: LoginViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    onFinished: () -> Unit,
    emailScreen: () -> Unit
) {
    var showLoading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewmodel.events.collect { event ->

            when (event) {
                is LoginViewModel.UiEvent.ShowError -> {
                    showLoading = false
                    snackbarHostState.showSnackbar(
                        message = event.message ?: ""
                    )
                }

                LoginViewModel.UiEvent.Success -> {
                    showLoading = false
                    onFinished()
                }

                LoginViewModel.UiEvent.Loading -> {
                    showLoading = true
                }
            }
        }
    }
    if(showLoading) Box(Modifier.fillMaxSize()){
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
    else Scaffold(
        modifier = Modifier.padding(16.dp),
        topBar = {
            Box(Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = {
                        onFinished()
                    },
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp).align(Alignment.TopEnd),

                    ) {
                    Text(
                        text = stringResource(Res.string.login_skip),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }
            }
        }
    )
    { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    top = 32.dp,
                    bottom = 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Luggage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.login_welcome),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Información sobre el registro
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(Res.string.login_registration_optional),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(Res.string.login_continue_with),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Google
            LoginProviderButton(
                text = stringResource(Res.string.login_google),
                icon = {

                },
                onClick = viewmodel::googleClick
            )

            Spacer(modifier = Modifier.height(12.dp))


            // Correo
            LoginProviderButton(
                text = stringResource(Res.string.login_email),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                onClick = emailScreen
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(Res.string.login_register_later),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoginProviderButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier.width(32.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Medium
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null
        )
    }
}