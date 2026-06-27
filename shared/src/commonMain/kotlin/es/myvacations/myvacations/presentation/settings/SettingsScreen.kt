package es.myvacations.myvacations.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import es.myvacations.myvacations.presentation.utils.AppDropDown
import es.myvacations.myvacations.presentation.utils.Currency
import es.myvacations.myvacations.presentation.utils.toCurrencyName
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    AppDropDown(
        items = Currency.entries,
        selectedItem = uiState.currency,
        onItemSelected = viewModel::updateCurrency,
        itemLabel = { it.name + " - " + it.toCurrencyName() }
    )
}