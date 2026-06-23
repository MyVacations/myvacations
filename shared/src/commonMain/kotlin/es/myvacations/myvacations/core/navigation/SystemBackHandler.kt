package es.myvacations.myvacations.core.navigation

import androidx.compose.runtime.Composable

@Composable
expect fun SystemBackHandler(onBack: () -> Unit)