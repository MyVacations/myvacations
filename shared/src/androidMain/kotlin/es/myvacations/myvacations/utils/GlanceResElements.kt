package es.myvacations.myvacations.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.presentation.utils.Currency
import es.myvacations.myvacations.shared.R


@DrawableRes
fun TripCover.glanceImageRes(): Int = when (this) {
    TripCover.TOKYO -> R.drawable.tokyo
    TripCover.PARIS -> R.drawable.paris
    TripCover.LONDON -> R.drawable.london
    TripCover.BARCELONA -> R.drawable.barcelona
    TripCover.ROME -> R.drawable.roma
    TripCover.BEACH -> R.drawable.beach
    TripCover.MOUNTAIN -> R.drawable.mountain
}

@StringRes
fun Currency.glanceStringRes(): Int = when (this) {
    Currency.DOLLAR -> R.string.dollar
    Currency.EURO -> R.string.euro
    Currency.YEN -> R.string.yen
    Currency.YUAN -> R.string.yuan
    Currency.PESO -> R.string.peso
}