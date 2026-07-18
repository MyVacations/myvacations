package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.trip_detail_budget
import myvacations.shared.generated.resources.trip_detail_budget_remaining
import myvacations.shared.generated.resources.trip_detail_header_spent
import org.jetbrains.compose.resources.stringResource

@Composable
fun LegendItem(
    color: Color,
    text: String,
    textColor: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    icon: ImageVector? = null,
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon == null) {
            Box(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(10.dp)
                    .background(color, CircleShape)
            )
        } else {
            Text("${stringResource(Res.string.trip_detail_budget_remaining)}:")
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = icon,
                contentDescription = "iconLegend",
                tint = color
            )
        }
        Spacer(Modifier.width(10.dp))

        Text(
            text = text,
            style = style,
            color = textColor
        )
    }
}