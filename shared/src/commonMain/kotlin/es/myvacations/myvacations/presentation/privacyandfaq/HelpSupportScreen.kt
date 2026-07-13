package es.myvacations.myvacations.presentation.privacyandfaq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.core.utils.AppInfo
import es.myvacations.myvacations.core.utils.openEmail
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.contact
import myvacations.shared.generated.resources.contact_description
import myvacations.shared.generated.resources.contact_email
import myvacations.shared.generated.resources.faq_create_trip_answer
import myvacations.shared.generated.resources.faq_create_trip_question
import myvacations.shared.generated.resources.faq_delete_trip_answer
import myvacations.shared.generated.resources.faq_delete_trip_question
import myvacations.shared.generated.resources.faq_edit_trip_answer
import myvacations.shared.generated.resources.faq_edit_trip_question
import myvacations.shared.generated.resources.faq_offline_answer
import myvacations.shared.generated.resources.faq_offline_question
import myvacations.shared.generated.resources.faq_sync_answer
import myvacations.shared.generated.resources.faq_sync_question
import myvacations.shared.generated.resources.help_intro_body
import myvacations.shared.generated.resources.help_intro_title
import myvacations.shared.generated.resources.support_email_subject
import myvacations.shared.generated.resources.title_support
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onDismiss: () -> Unit) {
    val faqItems = listOf(
        FaqItem(
            Res.string.faq_create_trip_question,
            Res.string.faq_create_trip_answer
        ),
        FaqItem(
            Res.string.faq_edit_trip_question,
            Res.string.faq_edit_trip_answer
        ),
        FaqItem(
            Res.string.faq_delete_trip_question,
            Res.string.faq_delete_trip_answer
        ),
        FaqItem(
            Res.string.faq_sync_question,
            Res.string.faq_sync_answer
        ),
        FaqItem(
            Res.string.faq_offline_question,
            Res.string.faq_offline_answer
        )
    )
    SystemBackHandler {
        onDismiss()
    }
    Scaffold(modifier = Modifier.padding(top = 12.dp), topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.title_support)) },
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
                HelpIntroCard()
            }
            items(faqItems) { faq ->
                ExpandableFaq(
                    question = stringResource(faq.question, AppInfo.appName),
                    answer = stringResource(faq.answer, AppInfo.appName)
                )
            }
            item {
                ContactCard(
                )
            }
        }
    }
}

@Composable
private fun HelpIntroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Default.HelpOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.help_intro_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.help_intro_body, AppInfo.appName),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExpandableFaq(
    question: String,
    answer: String
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        onClick = {
            expanded = !expanded
        }
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            AnimatedVisibility(expanded) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactCard(
) {
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

                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = stringResource(Res.string.contact),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.contact_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            val email = stringResource(Res.string.contact_email)
            val subject = stringResource(Res.string.support_email_subject)
            TextButton(
                onClick = {
                    openEmail(
                        email = email,
                        subject = subject
                    )
                }
            ) {
                Text(email)
            }
        }
    }
}