package es.myvacations.myvacations.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import es.myvacations.myvacations.presentation.chatbot.ElementsFoundUiState
import es.myvacations.myvacations.presentation.chatbot.LocationUiState
import es.myvacations.myvacations.presentation.chatbot.normalizePlaceType
import es.myvacations.myvacations.shared.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.maplibre.android.snapshotter.MapSnapshotter
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

object MapBitmapGenerator {

    suspend fun generateMapBitmap(
        context: Context,
        location: LocationUiState,
        places: List<ElementsFoundUiState>,
        width: Int,
        height: Int
    ): File? = withContext(Dispatchers.Main.immediate) {

        context.cacheDir
            .listFiles { file ->
                file.name.startsWith("widget_map_") &&
                        file.extension == "png"
            }
            ?.forEach { it.delete() }

        suspendCancellableCoroutine { continuation ->

            MapLibre.getInstance(context)

            val options = MapSnapshotter.Options(
                width,
                height
            )
                .withStyleBuilder(
                    Style.Builder()
                        .fromUri(
                            "https://tiles.openfreemap.org/styles/liberty"
                        )
                )
                .withCameraPosition(
                    CameraPosition.Builder()
                        .target(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        )
                        .zoom(16.5)
                        .build()
                )

            val snapshotter = MapSnapshotter(
                context,
                options
            )

            snapshotter.setObserver(
                object : MapSnapshotter.Observer {

                    override fun onDidFinishLoadingStyle() {
                    }

                    override fun onStyleImageMissing(imageName: String) {
                    }
                }
            )

            snapshotter.start(
                { snapshot ->

                    val file = File(
                        context.cacheDir,
                        "widget_map_${System.currentTimeMillis()}.png"
                    )

                    val bitmap = snapshot.bitmap.copy(
                        Bitmap.Config.ARGB_8888,
                        true
                    )

                    val canvas = Canvas(bitmap)

                    val point = latLngToPixel(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        centerLatitude = location.latitude,
                        centerLongitude = location.longitude,
                        zoom = 16.5,
                        width = width,
                        height = height
                    )

                    val drawable = AppCompatResources.getDrawable(
                        context,
                        R.drawable.walk
                    )

                    val iconSize = 24

                    drawable?.setBounds(
                        (point.x - iconSize / 2).toInt(),
                        (point.y - iconSize).toInt(),
                        (point.x + iconSize / 2).toInt(),
                        point.y.toInt()
                    )

                    drawable?.draw(canvas)

                    places.forEach { place ->
                        val point = latLngToPixel(
                            latitude = place.latitude,
                            longitude = place.longitude,
                            centerLatitude = location.latitude,
                            centerLongitude = location.longitude,
                            zoom = 16.5,
                            width = width,
                            height = height
                        )

                        if (
                            point.x < 0 ||
                            point.x > width ||
                            point.y < 0 ||
                            point.y > height
                        ) {
                            return@forEach
                        }

                        val drawable = getPlaceIcon(
                            context = context,
                            type = place.type
                        )

                        val iconSize = 24

                        drawable.setBounds(
                            (point.x - iconSize / 2).toInt(),
                            (point.y - iconSize).toInt(),
                            (point.x + iconSize / 2).toInt(),
                            point.y.toInt()
                        )

                        drawable.draw(canvas)
                    }

                    FileOutputStream(file).use { output ->
                        bitmap.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            output
                        )
                    }

                    continuation.resume(file) { _, _, _ -> }
                },
                { error ->
                    println("MAP SNAPSHOT ERROR: $error")

                    continuation.resume(null) { _, _, _ -> }
                }
            )

            continuation.invokeOnCancellation {
                snapshotter.cancel()
            }
        }
    }

    private fun latLngToPixel(
        latitude: Double,
        longitude: Double,
        centerLatitude: Double,
        centerLongitude: Double,
        zoom: Double,
        width: Int,
        height: Int
    ): PointF {

        val worldSize = 512.0 * 2.0.pow(zoom)

        fun mercatorX(longitude: Double): Double {
            return (longitude + 180.0) / 360.0 * worldSize
        }

        fun mercatorY(latitude: Double): Double {
            val latRad = Math.toRadians(
                latitude.coerceIn(-85.05112878, 85.05112878)
            )

            return (
                    (1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0
                    ) * worldSize
        }

        val centerX = mercatorX(centerLongitude)
        val centerY = mercatorY(centerLatitude)

        val pointX = mercatorX(longitude)
        val pointY = mercatorY(latitude)

        return PointF(
            (width / 2.0 + pointX - centerX).toFloat(),
            (height / 2.0 + pointY - centerY).toFloat()
        )
    }

    private fun getPlaceIcon(
        context: Context,
        type: String?
    ): Drawable {
        return when (normalizePlaceType(type ?: "")) {
            "restaurant" -> AppCompatResources.getDrawable(
                context,
                R.drawable.restaurant
            )

            "cafe" -> AppCompatResources.getDrawable(
                context,
                R.drawable.localcafe
            )

            "bar" -> AppCompatResources.getDrawable(
                context,
                R.drawable.localbar
            )

            "fuel" -> AppCompatResources.getDrawable(
                context,
                R.drawable.localgasstation
            )

            "park" -> AppCompatResources.getDrawable(
                context,
                R.drawable.park
            )

            "museum" -> AppCompatResources.getDrawable(
                context,
                R.drawable.museum
            )

            "meal" -> AppCompatResources.getDrawable(
                context,
                R.drawable.fastfood
            )

            "ice" -> AppCompatResources.getDrawable(
                context,
                R.drawable.icecream
            )

            else -> AppCompatResources.getDrawable(
                context,
                R.drawable.place
            )
        }!!
    }
}