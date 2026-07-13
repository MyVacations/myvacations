package es.myvacations.myvacations.presentation.tripdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.extensions.shortCurrency
import es.myvacations.myvacations.core.extensions.transformInInitials
import es.myvacations.myvacations.presentation.utils.toCurrencySymbol
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.accept
import myvacations.shared.generated.resources.cancel
import myvacations.shared.generated.resources.guest_user
import myvacations.shared.generated.resources.trip_detail_traveler_costperperson
import myvacations.shared.generated.resources.trip_detail_traveler_costyou
import myvacations.shared.generated.resources.trip_detail_traveler_delete
import myvacations.shared.generated.resources.trip_detail_traveler_name_placeholder
import myvacations.shared.generated.resources.trip_detail_traveler_newtraveler
import myvacations.shared.generated.resources.trip_detail_traveler_summay
import myvacations.shared.generated.resources.trip_detail_traveler_totalcost
import myvacations.shared.generated.resources.trip_detail_traveler_travelers
import org.jetbrains.compose.resources.stringResource

@Composable
fun TravelersScreen(
    uiState: TripDetailUiState,
    onTravelerNameChanged: (String, String, Boolean) -> Unit,
    onInsertTraveler: () -> Unit,
    onDeleteTraveler: (String, String) -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.trip_detail_traveler_summay),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(
                        Res.string.trip_detail_traveler_travelers,
                        uiState.tripUiState.travelers
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(20.dp))
            SummaryRow(
                title = stringResource(Res.string.trip_detail_traveler_totalcost),
                value = uiState.tripUiState.mainCost.shortCurrency() + " " + uiState.currency.toCurrencySymbol(),
            )

            Spacer(Modifier.height(12.dp))
            SummaryRow(
                title = stringResource(Res.string.trip_detail_traveler_costperperson),
                value = uiState.tripUiState.costPerPerson.shortCurrency() + " " + uiState.currency.toCurrencySymbol()
            )

            Spacer(Modifier.height(12.dp))
            SummaryRow(
                title = stringResource(Res.string.trip_detail_traveler_costyou),
                value = uiState.tripUiState.individualCost.shortCurrency() + " " + uiState.currency.toCurrencySymbol(),
                valueColor = MaterialTheme.colorScheme.primary
            )
        }
    }

    Spacer(modifier = Modifier.height(32.dp))
    Column {
        uiState.travelers.forEachIndexed { index, traveler ->
            val displayText = when {
                traveler.travelerName.isNotEmpty() -> traveler.travelerName
                index == 0 -> stringResource(Res.string.guest_user)
                else -> stringResource(
                    Res.string.trip_detail_traveler_name_placeholder,
                    index + 1
                )
            }
            var deleting by remember { mutableStateOf(false) }
            var editing by remember { mutableStateOf(false) }
            var newName by remember { mutableStateOf(displayText) }
            if (deleting) {
                AlertDialog(
                    onDismissRequest = { deleting = false },
                    title = {
                        Text(
                            stringResource(
                                Res.string.trip_detail_traveler_delete,
                                displayText
                            )
                        )
                    },
                    confirmButton = {
                        Text(
                            modifier = Modifier.clickable(onClick = {
                                deleting = false
                                onDeleteTraveler(traveler.id, uiState.tripUiState.id)
                            }),
                            text = stringResource(Res.string.accept),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    dismissButton = {
                        Text(modifier = Modifier.clickable(onClick = {
                            deleting = false
                        }), text = stringResource(Res.string.cancel))
                    }
                )
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(displayText.transformInInitials())
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    if (!editing) {
                        Text(modifier = Modifier.weight(1f), text = displayText)
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

                    // Precio + acciones
                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = (if (index == 0) uiState.tripUiState.individualCost.shortCurrency() else uiState.tripUiState.costPerPerson.shortCurrency()) + " " + uiState.currency.toCurrencySymbol(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(8.dp))

                        Row {
                            if (editing) {
                                FilledTonalIconButton(
                                    onClick = {
                                        editing = false
                                        onTravelerNameChanged(
                                            traveler.id,
                                            newName,
                                            traveler.isMainTraveler
                                        )
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
                                FilledTonalIconButton(
                                    onClick = { editing = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }

                                Spacer(Modifier.width(8.dp))
                                if (!traveler.isMainTraveler) {
                                    FilledTonalIconButton(
                                        onClick = { deleting = true },
                                        modifier = Modifier.size(36.dp),
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    TextButton(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        onClick = onInsertTraveler
    ) {
        Text(stringResource(Res.string.trip_detail_traveler_newtraveler))
    }
}

@Composable
private fun SummaryRow(
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}