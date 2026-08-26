package es.myvacations.myvacations.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
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

    private data class RadiusGeometry(
        val contours: List<List<Pair<Double, Double>>>
    )

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
            options.showAttribution = false

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
                    if (!continuation.isActive) return@start
                    val file = File(
                        context.cacheDir,
                        "widget_map_${System.currentTimeMillis()}.png"
                    )

                    val bitmap = snapshot.bitmap.copy(
                        Bitmap.Config.ARGB_8888,
                        true
                    )

                    val canvas = Canvas(bitmap)

                    val geometry = calculatePlacesRadiusGeometry(
                        location = location,
                        places = places,
                        radiusMeters = 500.0,
                        zoom = 16.5,
                        width = width,
                        height = height
                    )

                    val closedContours = closeContours(
                        geometry.contours
                    )

                    drawOutsideArea(
                        canvas = canvas,
                        contours = closedContours,
                        location = location,
                        width = width,
                        height = height,
                        zoom = 16.5
                    )

                    drawRadiusBorder(
                        canvas = canvas,
                        contours = closedContours,
                        location = location,
                        width = width,
                        height = height,
                        zoom = 16.5
                    )

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
                    if (!continuation.isActive) return@start

                    continuation.resume(null) { _, _, _ -> }
                }
            )

            continuation.invokeOnCancellation {
                runCatching {
                    snapshotter.cancel()
                }
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

    private fun calculatePlacesRadiusGeometry(
        location: LocationUiState,
        places: List<ElementsFoundUiState>,
        radiusMeters: Double,
        zoom: Double,
        width: Int,
        height: Int
    ): RadiusGeometry {

        if (places.isEmpty()) {
            return RadiusGeometry(emptyList())
        }

        val earthRadius = 6_371_000.0

        val referenceLatitude = location.latitude
        val referenceLongitude = location.longitude

        val latRadians =
            referenceLatitude * PI / 180.0

        fun toX(longitude: Double): Double {
            return (longitude - referenceLongitude) *
                    PI / 180.0 *
                    earthRadius *
                    cos(latRadians)
        }

        fun toY(latitude: Double): Double {
            return (latitude - referenceLatitude) *
                    PI / 180.0 *
                    earthRadius
        }

        fun toLongitude(x: Double): Double {
            return referenceLongitude +
                    x / (earthRadius * cos(latRadians)) *
                    180.0 / PI
        }

        fun toLatitude(y: Double): Double {
            return referenceLatitude +
                    y / earthRadius *
                    180.0 / PI
        }

        data class Point(
            val x: Double,
            val y: Double
        )

        data class Interval(
            val start: Double,
            val end: Double
        )

        val centers = places.map {
            Point(
                x = toX(it.longitude),
                y = toY(it.latitude)
            )
        }

        val fullCircle = 2.0 * PI

        val contours =
            mutableListOf<List<Pair<Double, Double>>>()

        centers.forEachIndexed { index, center ->

            val coveredIntervals =
                mutableListOf<Interval>()

            centers.forEachIndexed { otherIndex, other ->

                if (index == otherIndex) {
                    return@forEachIndexed
                }

                val dx = other.x - center.x
                val dy = other.y - center.y

                val distance = kotlin.math.sqrt(
                    dx * dx + dy * dy
                )

                if (distance >= radiusMeters * 2.0) {
                    return@forEachIndexed
                }

                if (distance < 0.000001) {
                    return@forEachIndexed
                }

                val direction =
                    kotlin.math.atan2(dy, dx)

                val halfAngle =
                    kotlin.math.acos(
                        distance / (2.0 * radiusMeters)
                    )

                var start =
                    direction - halfAngle

                var end =
                    direction + halfAngle

                while (start < 0.0) {
                    start += fullCircle
                    end += fullCircle
                }

                while (start >= fullCircle) {
                    start -= fullCircle
                    end -= fullCircle
                }

                if (end <= fullCircle) {

                    coveredIntervals += Interval(
                        start,
                        end
                    )

                } else {

                    coveredIntervals += Interval(
                        start,
                        fullCircle
                    )

                    coveredIntervals += Interval(
                        0.0,
                        end - fullCircle
                    )
                }
            }

            coveredIntervals.sortBy {
                it.start
            }

            val mergedIntervals =
                mutableListOf<Interval>()

            coveredIntervals.forEach { interval ->

                val last =
                    mergedIntervals.lastOrNull()

                if (
                    last == null ||
                    interval.start > last.end
                ) {

                    mergedIntervals += interval

                } else {

                    mergedIntervals[
                        mergedIntervals.lastIndex
                    ] = Interval(
                        last.start,
                        maxOf(
                            last.end,
                            interval.end
                        )
                    )
                }
            }

            val visibleIntervals =
                mutableListOf<Interval>()

            var current = 0.0

            mergedIntervals.forEach { interval ->

                if (current < interval.start) {

                    visibleIntervals += Interval(
                        current,
                        interval.start
                    )
                }

                current = maxOf(
                    current,
                    interval.end
                )
            }

            if (current < fullCircle) {

                visibleIntervals += Interval(
                    current,
                    fullCircle
                )
            }

            if (mergedIntervals.isEmpty()) {

                visibleIntervals.clear()

                visibleIntervals += Interval(
                    0.0,
                    fullCircle
                )
            }

            visibleIntervals.forEach { interval ->

                val angleLength =
                    interval.end - interval.start

                val segments = maxOf(
                    8,
                    (angleLength / (PI / 36.0)).toInt()
                )

                val contour =
                    (0..segments).map { i ->

                        val progress =
                            i.toDouble() / segments

                        val angle =
                            interval.start +
                                    angleLength * progress

                        val x =
                            center.x +
                                    radiusMeters *
                                    cos(angle)

                        val y =
                            center.y +
                                    radiusMeters *
                                    kotlin.math.sin(angle)

                        Pair(
                            toLongitude(x),
                            toLatitude(y)
                        )
                    }

                if (contour.size >= 2) {
                    contours += contour
                }
            }
        }

        return RadiusGeometry(
            contours = contours
        )
    }

    private fun closeContours(
        contours: List<List<Pair<Double, Double>>>
    ): List<List<Pair<Double, Double>>> {

        if (contours.isEmpty()) {
            return emptyList()
        }

        val remaining = contours
            .filter { it.size >= 2 }
            .map { it.toMutableList() }
            .toMutableList()

        val result =
            mutableListOf<List<Pair<Double, Double>>>()

        fun distance(
            a: Pair<Double, Double>,
            b: Pair<Double, Double>
        ): Double {
            val dx = a.first - b.first
            val dy = a.second - b.second

            return kotlin.math.sqrt(
                dx * dx + dy * dy
            )
        }

        val connectionTolerance = 0.00001

        while (remaining.isNotEmpty()) {

            val current =
                remaining.removeAt(0)

            if (
                distance(
                    current.first(),
                    current.last()
                ) < connectionTolerance
            ) {
                result += current
                continue
            }

            val ring =
                current.toMutableList()

            var changed = true

            while (changed) {

                changed = false

                val ringStart = ring.first()
                val ringEnd = ring.last()

                var foundIndex = -1
                var reverse = false

                remaining.forEachIndexed { index, contour ->

                    val contourStart = contour.first()
                    val contourEnd = contour.last()

                    when {

                        distance(
                            ringEnd,
                            contourStart
                        ) < connectionTolerance -> {

                            foundIndex = index
                            reverse = false
                        }

                        distance(
                            ringEnd,
                            contourEnd
                        ) < connectionTolerance -> {

                            foundIndex = index
                            reverse = true
                        }
                    }

                    if (foundIndex != -1) {
                        return@forEachIndexed
                    }
                }

                if (foundIndex != -1) {

                    val next =
                        remaining.removeAt(foundIndex)

                    if (reverse) {
                        next.reverse()
                    }

                    ring.addAll(
                        next.drop(1)
                    )

                    changed = true

                } else {

                    remaining.forEachIndexed { index, contour ->

                        val contourStart = contour.first()
                        val contourEnd = contour.last()

                        when {

                            distance(
                                ringStart,
                                contourEnd
                            ) < connectionTolerance -> {

                                foundIndex = index
                                reverse = false
                            }

                            distance(
                                ringStart,
                                contourStart
                            ) < connectionTolerance -> {

                                foundIndex = index
                                reverse = true
                            }
                        }

                        if (foundIndex != -1) {
                            return@forEachIndexed
                        }
                    }

                    if (foundIndex != -1) {

                        val next =
                            remaining.removeAt(foundIndex)

                        if (reverse) {
                            next.reverse()
                        }

                        ring.addAll(
                            0,
                            next.dropLast(1)
                        )

                        changed = true
                    }
                }
            }

            if (
                distance(
                    ring.first(),
                    ring.last()
                ) >= connectionTolerance
            ) {
                ring.add(ring.first())
            }

            result += ring
        }

        return result
    }

    private fun drawOutsideArea(
        canvas: Canvas,
        contours: List<List<Pair<Double, Double>>>,
        location: LocationUiState,
        width: Int,
        height: Int,
        zoom: Double
    ) {
        if (contours.isEmpty()) return

        val outsidePath = Path()

        outsidePath.addRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            Path.Direction.CW
        )

        contours.forEach { contour ->

            if (contour.size < 3) {
                return@forEach
            }

            val path = Path()

            contour.forEachIndexed { index, coordinate ->

                val point = latLngToPixel(
                    latitude = coordinate.second,
                    longitude = coordinate.first,
                    centerLatitude = location.latitude,
                    centerLongitude = location.longitude,
                    zoom = zoom,
                    width = width,
                    height = height
                )

                if (index == 0) {
                    path.moveTo(
                        point.x,
                        point.y
                    )
                } else {
                    path.lineTo(
                        point.x,
                        point.y
                    )
                }
            }

            path.close()

            outsidePath.op(
                path,
                Path.Op.DIFFERENCE
            )
        }

        val paint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = android.graphics.Color.BLACK
            alpha = 115
            style = Paint.Style.FILL
        }

        canvas.drawPath(
            outsidePath,
            paint
        )
    }

    private fun drawRadiusBorder(
        canvas: Canvas,
        contours: List<List<Pair<Double, Double>>>,
        location: LocationUiState,
        width: Int,
        height: Int,
        zoom: Double
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.BLUE
            alpha = 255
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        contours.forEach { contour ->

            if (contour.size < 2) {
                return@forEach
            }

            val path = Path()

            contour.forEachIndexed { index, coordinate ->

                val point = latLngToPixel(
                    latitude = coordinate.second,
                    longitude = coordinate.first,
                    centerLatitude = location.latitude,
                    centerLongitude = location.longitude,
                    zoom = zoom,
                    width = width,
                    height = height
                )

                if (index == 0) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }
            }

            path.close()

            canvas.drawPath(
                path,
                paint
            )
        }
    }
}