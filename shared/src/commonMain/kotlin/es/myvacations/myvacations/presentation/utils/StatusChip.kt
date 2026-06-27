package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.myvacations.myvacations.domain.model.TripStatus
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.actual_trip_noone
import myvacations.shared.generated.resources.actual_trip_past
import myvacations.shared.generated.resources.actual_trip_predict
import myvacations.shared.generated.resources.actual_trip_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatusChip(
    status: TripStatus,
) {
    when (status) {
        TripStatus.PLANNED ->{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlinkingCircle(Color(0xFF7C4DFF))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(Res.string.actual_trip_predict),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
        TripStatus.ACTIVE -> {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlinkingCircle(Color(0xFF00E676))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(Res.string.actual_trip_title),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

        }
        TripStatus.COMPLETE -> {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlinkingCircle(Color(0xFFB0B630))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(Res.string.actual_trip_past),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}