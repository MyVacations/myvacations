package es.myvacations.myvacations.presentation.privacyandfaq

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.core.utils.AppInfo
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.privacy_changes_body
import myvacations.shared.generated.resources.privacy_changes_title
import myvacations.shared.generated.resources.privacy_contact_body
import myvacations.shared.generated.resources.privacy_contact_title
import myvacations.shared.generated.resources.privacy_information_body
import myvacations.shared.generated.resources.privacy_information_title
import myvacations.shared.generated.resources.privacy_internet_body
import myvacations.shared.generated.resources.privacy_internet_title
import myvacations.shared.generated.resources.privacy_intro_body
import myvacations.shared.generated.resources.privacy_intro_title
import myvacations.shared.generated.resources.privacy_last_update
import myvacations.shared.generated.resources.privacy_permissions_body
import myvacations.shared.generated.resources.privacy_permissions_title
import myvacations.shared.generated.resources.privacy_policy
import myvacations.shared.generated.resources.privacy_security_body
import myvacations.shared.generated.resources.privacy_security_title
import myvacations.shared.generated.resources.privacy_sharing_body
import myvacations.shared.generated.resources.privacy_sharing_title
import myvacations.shared.generated.resources.privacy_storage_body
import myvacations.shared.generated.resources.privacy_storage_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyScreen(onDismiss: () -> Unit) {
    SystemBackHandler {
        onDismiss()
    }
    val sections = rememberPolicySections()
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.privacy_policy)) },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null)
                }
            }
        )
    }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    stringResource(Res.string.privacy_last_update),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            items(sections) {
                PrivacyCard(
                    title = it.title,
                    body = it.body
                )
            }
        }
    }
}

@Composable
private fun rememberPolicySections(): List<PolicySections> =
    listOf(
        PolicySections(
            title = stringResource(Res.string.privacy_intro_title),
            body = stringResource(Res.string.privacy_intro_body,AppInfo.appName)
        ),
        PolicySections(
            title = stringResource(Res.string.privacy_information_title),
            body = stringResource(Res.string.privacy_information_body,AppInfo.appName)
        ),
        PolicySections(
            title = stringResource(Res.string.privacy_storage_title),
            body = stringResource(Res.string.privacy_storage_body)
        ),
        PolicySections(
            title = stringResource(Res.string.privacy_internet_title),
            body = stringResource(Res.string.privacy_internet_body)
        ),
        PolicySections(
            title = stringResource(Res.string.privacy_sharing_title),
            body = stringResource(Res.string.privacy_sharing_body,AppInfo.appName)
        ),
        PolicySections(
            title = stringResource(Res.string.privacy_permissions_title),
            body = stringResource(Res.string.privacy_permissions_body)
        ),
        PolicySections(
            title = stringResource(Res.string.privacy_security_title),
            body = stringResource(Res.string.privacy_security_body)
        ),
        PolicySections(
            title = stringResource(Res.string.privacy_changes_title),
            body = stringResource(Res.string.privacy_changes_body)
        ),
        PolicySections(
            title = stringResource(Res.string.privacy_contact_title),
            body = stringResource(Res.string.privacy_contact_body)
        )
    )

@Composable
private fun PrivacyCard(
    title: String,
    body: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}