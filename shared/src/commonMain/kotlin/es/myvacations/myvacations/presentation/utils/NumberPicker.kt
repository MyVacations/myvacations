package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun NumberPicker(
    value: Int = 0,
    onValueChange: (Int) -> Unit = {},
    min: Int = 0,
    max: Int = Int.MAX_VALUE
) {
    Row(
        modifier = Modifier
            .height(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1E1E)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString()
            )
        }
        Column {
            IconButton(
                modifier = Modifier.size(28.dp),
                onClick = {
                    if (value < max) {
                        onValueChange(value + 1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowUp, null)
            }

            IconButton(
                modifier = Modifier.size(28.dp),
                onClick = {
                    if (value > min) {
                        onValueChange(value - 1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowDown, null)
            }
        }
    }
}