package es.myvacations.myvacations.presentation.login

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.ai_tutorial_continue
import myvacations.shared.generated.resources.email
import myvacations.shared.generated.resources.forgot_password
import myvacations.shared.generated.resources.icono
import myvacations.shared.generated.resources.login_header
import myvacations.shared.generated.resources.password
import myvacations.shared.generated.resources.register_no_account
import myvacations.shared.generated.resources.repeat_password
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun EmailScreen(
    viewmodel: LoginViewModel = koinViewModel(),
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewmodel.uiState.collectAsState()
    var register by remember { mutableStateOf(false) }

    val navigationState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None
    )
    LaunchedEffect(uiState.isSuccess, uiState.error) {

        when {
            uiState.isSuccess -> onBack()

            uiState.error != null -> {
                snackbarHostState.showSnackbar(
                    message = uiState.error!!
                )
            }
        }
    }

    NavigationBackHandler(
        state = navigationState,
        isBackEnabled = true,
        onBackCompleted = {
            if (register) {
                register = false
            } else {
                onBack()
            }
        }
    )

    Scaffold(
        modifier = Modifier.padding(16.dp),
        topBar = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { if (register) register = false else onBack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.weight(0.8f))
                Text(stringResource(if (register) Res.string.register_no_account else Res.string.login_header))
                Spacer(Modifier.weight(1f))
            }
        },
        bottomBar = {
            if (!register) {
                Box(Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = { register = true },
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp).align(Alignment.Center),
                    ) {
                        Text(text = stringResource(Res.string.register_no_account))
                    }
                }
            }
        }
    )
    { paddingValues ->
        if (register) {
            RegisterEmailComponent(
                modifier = Modifier.padding(paddingValues),
                onRegister = { email, password ->
                    viewmodel.registerWithEmail(
                        email = email,
                        password = password
                    )
                }
            )
        } else {
            LoginEmailComponent(
                modifier = Modifier.padding(paddingValues),
                onLogin = { email, password ->
                    viewmodel.loginWithEmail(
                        email = email,
                        password = password
                    )
                },
                onReset = { email ->
                    viewmodel.resetPassword(
                        email = email,
                    )
                }
            )
        }
    }
}

@Composable
fun LoginEmailComponent(
    modifier: Modifier,
    onLogin: (String, String) -> Unit,
    onReset: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(Res.drawable.icono),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(84.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(stringResource(Res.string.email))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(stringResource(Res.string.password))
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onLogin(
                    email.trim(),
                    password
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text(
                text = stringResource(Res.string.ai_tutorial_continue)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { onReset(email.trim()) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(
                    Res.string.forgot_password
                )
            )
        }
    }
}

@Composable
fun RegisterEmailComponent(modifier: Modifier, onRegister: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    val passwordsMatch =
        password == repeatPassword

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(
                    stringResource(Res.string.email)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(
                    stringResource(Res.string.password)
                )
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = {
                repeatPassword = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(
                    stringResource(Res.string.repeat_password)
                )
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onRegister(
                    email.trim(),
                    password
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled =
                email.isNotBlank() &&
                        password.isNotBlank() &&
                        repeatPassword.isNotBlank() &&
                        passwordsMatch
        ) {
            Text(
                text = stringResource(
                    Res.string.register_no_account
                )
            )
        }
    }
}