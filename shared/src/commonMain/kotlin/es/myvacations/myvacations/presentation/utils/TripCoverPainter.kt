package es.myvacations.myvacations.presentation.utils

import androidx.compose.runtime.Composable
import es.myvacations.myvacations.domain.model.TripCover
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun TripCover.painter() = when (this) {
    TripCover.TOKYO -> painterResource(Res.drawable.tokyo)
    TripCover.PARIS -> painterResource(Res.drawable.paris)
    TripCover.LONDON -> painterResource(Res.drawable.london)
    TripCover.BARCELONA -> painterResource(Res.drawable.barcelona)
    TripCover.ROME -> painterResource(Res.drawable.roma)
    TripCover.BEACH -> painterResource(Res.drawable.beach)
    TripCover.MOUNTAIN -> painterResource(Res.drawable.mountain)
}