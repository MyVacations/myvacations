package es.myvacations.myvacations.presentation.utils

import androidx.compose.runtime.Composable
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.dollar
import myvacations.shared.generated.resources.dollar_name
import myvacations.shared.generated.resources.euro
import myvacations.shared.generated.resources.euro_name
import myvacations.shared.generated.resources.peso
import myvacations.shared.generated.resources.peso_name
import myvacations.shared.generated.resources.yen
import myvacations.shared.generated.resources.yen_name
import myvacations.shared.generated.resources.yuan
import myvacations.shared.generated.resources.yuan_name
import org.jetbrains.compose.resources.stringResource

enum class Currency {
    DOLLAR, EURO, YEN, YUAN, PESO
}

@Composable
fun Currency.toCurrencySymbol(): String = when (this) {
    Currency.DOLLAR -> stringResource(Res.string.dollar)
    Currency.EURO -> stringResource(Res.string.euro)
    Currency.YEN -> stringResource(Res.string.yen)
    Currency.YUAN -> stringResource(Res.string.yuan)
    Currency.PESO -> stringResource(Res.string.peso)
}

@Composable
fun Currency.toCurrencyName(): String = when (this) {
    Currency.DOLLAR -> stringResource(Res.string.dollar_name)
    Currency.EURO -> stringResource(Res.string.euro_name)
    Currency.YEN -> stringResource(Res.string.yen_name)
    Currency.YUAN -> stringResource(Res.string.yuan_name)
    Currency.PESO -> stringResource(Res.string.peso_name)
}