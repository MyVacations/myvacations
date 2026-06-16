package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AlertDialogIcons(
    show: Boolean,
    onDismiss: () -> Unit,
    onIconSelected: (ImageVector) -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {},
            title = {
                Text("Selecciona un icono")
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(
                        TravelIcons.all
                    ) { item ->
                        IconButton(
                            onClick = {
                                onIconSelected(item)
                                onDismiss()
                            }
                        ) {
                            Icon(
                                imageVector = item,
                                contentDescription = item.name
                            )
                        }
                    }
                }
            })
    }
}