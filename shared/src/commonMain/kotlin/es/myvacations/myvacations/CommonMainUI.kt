package es.myvacations.myvacations

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import es.myvacations.myvacations.core.navigation.NavigationRoot

@Composable
fun App() {
    MaterialTheme {
        NavigationRoot()
    }
}