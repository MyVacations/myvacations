package es.myvacations.myvacations.presentation.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ChartItem(
    val name: String,
    val value: Double,
    val color: Color,
    val icon: ImageVector
)