package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.new_trip_food
import org.jetbrains.compose.resources.stringResource

@Preview(showBackground = true)
@Composable
fun ExpenseItem(
    expense: TripExpenseUiState = TripExpenseUiState(),
    onDelete: () -> Unit = {},
    openEditDialog: MutableState<Boolean> = mutableStateOf(false)
) {
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
}
