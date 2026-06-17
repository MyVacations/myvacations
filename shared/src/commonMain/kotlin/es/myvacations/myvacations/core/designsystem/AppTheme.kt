package es.myvacations.myvacations.core.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00A884),
    secondary = Color(0xFF00A884),
    tertiary = Color(0xFF00A884),

    background = Color(0xFFF5F7F8),
    surface = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00A884),
    secondary = Color(0xFF00A884),
    tertiary = Color(0xFF00A884),

    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)