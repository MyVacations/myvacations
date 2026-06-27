package es.myvacations.myvacations.presentation.utils

import androidx.compose.runtime.Composable
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.dollar
import myvacations.shared.generated.resources.euro
import myvacations.shared.generated.resources.peso
import myvacations.shared.generated.resources.trip_detail_expenses
import myvacations.shared.generated.resources.trip_detail_overview
import myvacations.shared.generated.resources.trip_detail_travelers
import myvacations.shared.generated.resources.yen
import myvacations.shared.generated.resources.yuan
import org.jetbrains.compose.resources.stringResource

enum class Currency {
    DOLLAR, EURO, YEN, YUAN, PESO
}

@Composable
fun Currency.toCurrencyName(): String = when (this) {
    Currency.DOLLAR -> stringResource(Res.string.dollar)
    Currency.EURO -> stringResource(Res.string.euro)
    Currency.YEN -> stringResource(Res.string.yen)
    Currency.YUAN -> stringResource(Res.string.yuan)
    Currency.PESO -> stringResource(Res.string.peso)
}