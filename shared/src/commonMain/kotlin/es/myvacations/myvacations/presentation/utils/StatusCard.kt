package es.myvacations.myvacations.presentation.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import myvacations.shared.generated.resources.actual_trip_past
import myvacations.shared.generated.resources.actual_trip_predict
import myvacations.shared.generated.resources.actual_trip_title
import myvacations.shared.generated.resources.trip_detail_active
import myvacations.shared.generated.resources.trip_detail_past
import myvacations.shared.generated.resources.trip_detail_upcoming
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatusCard(
    status: TripStatus,
) {
    when (status) {
        TripStatus.PLANNED ->{
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF7C4DFF)
            ) {
                Text(
                    text = stringResource(Res.string.trip_detail_upcoming).uppercase(),
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    ),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        TripStatus.ACTIVE -> {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF00E676)
            ) {
                Text(
                    text = stringResource(Res.string.trip_detail_active).uppercase(),
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    ),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        TripStatus.COMPLETE -> {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFEEFF80)
            ) {
                Text(
                    text = stringResource(Res.string.trip_detail_past).uppercase(),
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    ),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}