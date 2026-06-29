package es.myvacations.myvacations.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.myvacations.myvacations.core.extensions.transformInInitials
import es.myvacations.myvacations.core.utils.AppInfo
import es.myvacations.myvacations.presentation.utils.Currency
import es.myvacations.myvacations.presentation.utils.toCurrencyName
import es.myvacations.myvacations.presentation.utils.toCurrencySymbol
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.currency
import myvacations.shared.generated.resources.guest_user
import myvacations.shared.generated.resources.settings
import myvacations.shared.generated.resources.settings_about
import myvacations.shared.generated.resources.settings_preferences
import myvacations.shared.generated.resources.settings_privacy
import myvacations.shared.generated.resources.settings_support
import myvacations.shared.generated.resources.settings_version
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onPolicyScreen: () -> Unit,
    onHelpAndSupport: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var appDropDown by remember {
        mutableStateOf(false)
    }
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(Res.string.settings_version) + " " + AppInfo.versionName)
                    Text("© " + AppInfo.appName)
                }
            })
        { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.settings),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    ProfileCard(
                        name = uiState.userName,
                        viewModel::updateName
                    )
                }

                item {
                    SettingsSection(
                        title = stringResource(Res.string.settings_preferences)
                    ) {
                        SettingsItem(
                            icon = Icons.Default.AttachMoney,
                            title = stringResource(Res.string.currency),
                            subtitle = uiState.currency.toCurrencyName() + " - " + uiState.currency.toCurrencySymbol(),
                            showArrow = true,
                            onClick = {
                                appDropDown = true
                            }
                        )
                        DropdownMenu(
                            modifier = Modifier.width(260.dp),
                            expanded = appDropDown,
                            onDismissRequest = {
                                appDropDown = false
                            }
                        ) {
                            Currency.entries.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Text(item.toCurrencyName())
                                    },
                                    onClick = {
                                        viewModel.updateCurrency(item)
                                        appDropDown = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsSection(
                        title = stringResource(Res.string.settings_about)
                    ) {

                        SettingsItem(
                            icon = Icons.Default.Shield,
                            title = stringResource(Res.string.settings_privacy),
                            showArrow = true,
                            onClick = onPolicyScreen
                        )

                        HorizontalDivider()

                        SettingsItem(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            title = stringResource(Res.string.settings_support),
                            showArrow = true,
                            onClick = onHelpAndSupport
                        )

                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    name: String,
    updateName: (String) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    val name = name.ifBlank { stringResource(Res.string.guest_user) }
    var newName by remember { mutableStateOf(name) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                shape = CircleShape
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        color = Color.Black,
                        text = name.transformInInitials().takeIf { it.isNotBlank() }
                            ?: stringResource(Res.string.guest_user).transformInInitials())
                }
            }
            Spacer(Modifier.width(16.dp))
            if (!editing) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = name.takeIf { it.isNotBlank() }
                        ?: stringResource(Res.string.guest_user))
            } else {
                OutlinedTextField(
                    value = newName,
                    enabled = editing,
                    onValueChange = {
                        newName = it
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
            if (editing) {
                FilledTonalIconButton(
                    onClick = {
                        editing = false
                        updateName(newName)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(8.dp))
                FilledTonalIconButton(
                    onClick = { editing = false },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null
                    )
                }
            } else {
                Spacer(Modifier.width(8.dp))
                FilledTonalIconButton(
                    onClick = { editing = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingText: String? = null,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = .08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        when {
            trailingText != null ->
                Text(
                    text = trailingText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            showArrow ->
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
    }
}