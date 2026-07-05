package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.cancel
import myvacations.shared.generated.resources.new_trip_expense
import myvacations.shared.generated.resources.new_trip_food
import myvacations.shared.generated.resources.new_trip_optional_amount
import myvacations.shared.generated.resources.new_trip_optional_delete
import myvacations.shared.generated.resources.new_trip_optional_name
import myvacations.shared.generated.resources.new_trip_optional_name_error
import myvacations.shared.generated.resources.new_trip_save_trip
import org.jetbrains.compose.resources.stringResource

@Preview(showBackground = true)
@Composable
fun ExpenseItem(
    expense: TripExpenseUiState = TripExpenseUiState(),
    onDelete: () -> Unit = {},
    onUpdateExpenseName: (String, String) -> Unit = { _, _ -> },
    onUpdateExpenseIcon: (String, TravelIcon) -> Unit = { _, _ -> },
    onUpdateExpenseAmount: (String, String) -> Unit = { _, _ -> },
) {
    val name = expense.name.ifBlank { stringResource(Res.string.new_trip_food) }
    val onExpenseName = remember { mutableStateOf(name) }
    val onExpenseIcon = remember { mutableStateOf(expense.icon) }
    var textCost by rememberSaveable {
        mutableStateOf(
            if (expense.amount == 0.0) "" else expense.amount.toString()
        )
    }
    val openEditDialog = remember { mutableStateOf(false) }
    val colorOfIcon = expense.icon.toImageVector().iconColor()

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = colorOfIcon.copy(alpha = 0.15f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = expense.icon.toImageVector(),
                    contentDescription = null,
                    tint = colorOfIcon
                )
            }
        }

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = expense.name.ifBlank { stringResource(Res.string.new_trip_food) },
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${expense.amount} ${expense.currency.toCurrencySymbol()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = { openEditDialog.value = true }) {
            Icon(
                imageVector = Icons.Default.Edit, contentDescription = "Edit"
            )
        }

        IconButton(
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }

    if (openEditDialog.value) {
        AlertDialog(
            onDismissRequest = { openEditDialog.value = false }, title = {
                Text(stringResource(Res.string.new_trip_expense))
            },

            text = {
                Column {
                    Text(stringResource(Res.string.new_trip_optional_name))
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    OutlinedTextField(
                        value = onExpenseName.value, onValueChange = {
                            onExpenseName.value = it
                        }, singleLine = true
                    )
                    if (onExpenseName.value.isBlank()) {
                        Text(
                            text = stringResource(Res.string.new_trip_optional_name_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                    Text(
                        stringResource(
                            Res.string.new_trip_optional_amount,
                            expense.currency.toCurrencySymbol()
                        )
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    BasicTextField(
                        value = textCost,
                        onValueChange = { newValue ->
                            textCost = newValue
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        cursorBrush = SolidColor(Color.White),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.White, shape = RoundedCornerShape(6.dp))
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 16.dp
                                    )
                            ) {
                                if (textCost == "0.0" || textCost.isEmpty()) {
                                    Text(
                                        text = "0.0",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                innerTextField()
                            }
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4), modifier = Modifier.height(200.dp)
                    ) {
                        items(
                            TravelIcon.entries.size
                        ) { index ->
                            val icon = TravelIcon.entries[index]
                            Surface(
                                modifier = Modifier.padding(4.dp).size(48.dp).clickable {
                                    onExpenseIcon.value = icon
                                },
                                shape = CircleShape,
                                color = if (onExpenseIcon.value == icon) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.5f
                                )
                                else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon.toImageVector(),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        if ((onExpenseName.value.isBlank() || textCost == "0.0").not()) {
                            onUpdateExpenseName(expense.id, onExpenseName.value)
                            onUpdateExpenseAmount(expense.id, textCost)
                            onUpdateExpenseIcon(expense.id, onExpenseIcon.value)
                            openEditDialog.value = false
                        }
                    }) {
                    Text(stringResource(Res.string.new_trip_save_trip))
                }
            },

            dismissButton = {
                Row {
                    TextButton(
                        onClick = onDelete
                    ) {
                        Text(
                            stringResource(Res.string.new_trip_optional_delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    TextButton(
                        onClick = {
                            onExpenseName.value = expense.name
                            onExpenseIcon.value = expense.icon
                            openEditDialog.value = false
                        }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            })
    }
}
