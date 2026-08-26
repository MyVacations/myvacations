package es.myvacations.myvacations.presentation.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.South
import androidx.compose.material.icons.filled.West
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import es.myvacations.myvacations.core.extensions.roundTo4Decimals
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.activethefinelocation
import myvacations.shared.generated.resources.outoflimits
import myvacations.shared.generated.resources.preciseubication
import org.jetbrains.compose.resources.stringResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.case
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature.Companion.getStringProperty
import org.maplibre.spatialk.geojson.Position

data class RadiusGeometry(
    val contours: List<List<Pair<Double, Double>>>
)

@Composable
fun MapScreen(
    uiState: ChatUiState,
    chatMessage: ChatMessageUiState,
    updateDialogRequestingLocationPermissions: () -> Unit,
    itemSelected: (ElementsFoundUiState) -> Unit,
    loadMapUpdate: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapKey by remember { mutableIntStateOf(0) }
    var wasPaused by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->

            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    wasPaused = true
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (wasPaused) {
                        mapKey++
                        wasPaused = false
                    }
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val scope = rememberCoroutineScope()
    var openExtendedMap by rememberSaveable { mutableStateOf(false) }
    val places = chatMessage.bot?.elementsFound ?: return
    val firstLocation = chatMessage.locationFor500m
    val location = (if (uiState.outOfLimits) firstLocation else uiState.updatedLocation) ?: return

    val geoJson = remember(places) {
        placesToGeoJson(places)
    }

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(
                latitude = location.latitude.roundTo4Decimals(),
                longitude = location.longitude.roundTo4Decimals()
            ),
            zoom = 14.0
        )
    )

    val extendedCameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(
                latitude = location.latitude.roundTo4Decimals(),
                longitude = location.longitude.roundTo4Decimals()
            ),
            zoom = 14.0
        )
    )

    LaunchedEffect(
        location.latitude,
        location.longitude
    ) {
        val newPosition = CameraPosition(
            target = Position(
                latitude = location.latitude.roundTo4Decimals(),
                longitude = location.longitude.roundTo4Decimals()
            ),
            zoom = cameraState.position.zoom
        )

        cameraState.position = newPosition
        extendedCameraState.position = newPosition
    }

    LaunchedEffect(openExtendedMap) {

        if (openExtendedMap) {

            extendedCameraState.position = cameraState.position

            snapshotFlow {
                extendedCameraState.position
            }.collect { position ->

                if (cameraState.position != position) {
                    cameraState.position = position
                }
            }

        } else {

            cameraState.position = extendedCameraState.position

            snapshotFlow {
                cameraState.position
            }.collect { position ->

                if (extendedCameraState.position != position) {
                    extendedCameraState.position = position
                }
            }
        }
    }

    if (openExtendedMap) {
        Dialog(onDismissRequest = { openExtendedMap = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize().padding(vertical = 16.dp)
            ) {
                key(mapKey) {
                    MaplibreMap(
                        modifier = Modifier.fillMaxSize(),
                        baseStyle = BaseStyle.Uri(
                            "https://tiles.openfreemap.org/styles/liberty"
                        ),
                        options = MapOptions(
                            gestureOptions = GestureOptions.RotationLocked,
                            ornamentOptions = OrnamentOptions.OnlyLogo
                        ),
                        cameraState = extendedCameraState
                    )
                    {
                        LoadInsideMap(
                            uiState, geoJson, location,
                            itemSelected = { idSelected ->
                                places.find { it.id == idSelected }?.let { itemSelected(it) }
                            },
                            places = places,
                        )
                    }
                }
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        ).size(32.dp),
                    onClick = {
                        openExtendedMap = false
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close", tint = Color.White
                    )
                }
            }
            if (uiState.outOfLimits) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.outoflimits),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            MaplibreMap(
                modifier = Modifier.fillMaxSize(),
                baseStyle = BaseStyle.Uri(
                    "https://tiles.openfreemap.org/styles/liberty"
                ),
                options = MapOptions(
                    gestureOptions = GestureOptions.AllDisabled,
                    ornamentOptions = OrnamentOptions.OnlyLogo
                ),
                cameraState = cameraState,
                onMapLoadFinished = {
                    loadMapUpdate()
                },
                onMapLoadFailed = {
                    loadMapUpdate()
                }
            )
            {
                LoadInsideMap(
                    uiState, geoJson, location,
                    itemSelected = { idSelected ->
                        places.find { it.id == idSelected }?.let { itemSelected(it) }
                    },
                    places = places,
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        ).size(32.dp),
                    onClick = {
                        scope.launch {
                            cameraState.animateTo(
                                CameraPosition(
                                    target = cameraState.position.target,
                                    zoom = cameraState.position.zoom + 1
                                )
                            )
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.ZoomIn,
                        contentDescription = "Ampliar", tint = Color.White
                    )
                }
                Spacer(Modifier.height(6.dp))
                IconButton(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        ).size(32.dp),
                    onClick = {
                        scope.launch {
                            cameraState.animateTo(
                                CameraPosition(
                                    target = cameraState.position.target,
                                    zoom = cameraState.position.zoom - 1
                                )
                            )
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.ZoomOut,
                        contentDescription = "Reducir", tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        ).size(32.dp), onClick = {
                        scope.launch {
                            val position = cameraState.position
                            cameraState.animateTo(
                                CameraPosition(
                                    target = Position(
                                        latitude = position.target.latitude + 0.004,
                                        longitude = position.target.longitude
                                    ),
                                    zoom = cameraState.position.zoom
                                )
                            )
                        }
                    })
                {
                    Icon(
                        Icons.Default.North,
                        contentDescription = "nort",
                        tint = Color.White
                    )
                }

                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(8.dp)
                            ).size(32.dp), onClick = {
                            scope.launch {
                                val position = cameraState.position
                                cameraState.animateTo(
                                    CameraPosition(
                                        target = Position(
                                            latitude = position.target.latitude,
                                            longitude = position.target.longitude - 0.004
                                        ),
                                        zoom = cameraState.position.zoom
                                    )
                                )
                            }
                        })
                    {
                        Icon(
                            Icons.Default.West,
                            contentDescription = "west", tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    IconButton(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(8.dp)
                            ).size(32.dp), onClick = {
                            scope.launch {
                                val position = cameraState.position
                                cameraState.animateTo(
                                    CameraPosition(
                                        target = Position(
                                            latitude = position.target.latitude,
                                            longitude = position.target.longitude + 0.004
                                        ),
                                        zoom = cameraState.position.zoom
                                    )
                                )
                            }
                        })
                    {
                        Icon(
                            Icons.Default.East,
                            contentDescription = "east", tint = Color.White
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                IconButton(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        ).size(32.dp), onClick = {
                        scope.launch {
                            val position = cameraState.position
                            cameraState.animateTo(
                                CameraPosition(
                                    target = Position(
                                        latitude = position.target.latitude - 0.004,
                                        longitude = position.target.longitude
                                    ),
                                    zoom = cameraState.position.zoom
                                )
                            )
                        }
                    })
                {
                    Icon(
                        Icons.Default.South,
                        contentDescription = "south",
                        tint = Color.White
                    )
                }
            }
            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp)
                    ).size(32.dp), onClick = {
                    openExtendedMap = true
                })
            {
                Icon(
                    Icons.Default.ZoomOutMap,
                    contentDescription = "zoomout",
                    tint = Color.White
                )
            }
            if (!uiState.isFineLocationOn) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                        ).clickable {
                            updateDialogRequestingLocationPermissions()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.preciseubication),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = stringResource(Res.string.activethefinelocation),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            if (uiState.outOfLimits) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(Res.string.outoflimits),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
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

    val result = mutableListOf<List<Pair<Double, Double>>>()

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

    // Aproximadamente 1 metro en coordenadas geográficas.
    val connectionTolerance = 0.00001

    while (remaining.isNotEmpty()) {

        val current = remaining.removeAt(0)

        // Si ya es un círculo cerrado.
        if (
            distance(
                current.first(),
                current.last()
            ) < connectionTolerance
        ) {
            result += current
            continue
        }

        val ring = current.toMutableList()

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
                    distance(ringEnd, contourStart) <
                            connectionTolerance -> {

                        foundIndex = index
                        reverse = false
                    }

                    distance(ringEnd, contourEnd) <
                            connectionTolerance -> {

                        foundIndex = index
                        reverse = true
                    }
                }

                if (foundIndex != -1) {
                    return@forEachIndexed
                }
            }

            if (foundIndex != -1) {

                val next = remaining.removeAt(foundIndex)

                if (reverse) {
                    next.reverse()
                }

                // Evitamos duplicar el punto de unión.
                ring.addAll(next.drop(1))

                changed = true
            } else {

                // También comprobamos si tenemos que unir
                // por el principio del anillo.
                remaining.forEachIndexed { index, contour ->

                    val contourStart = contour.first()
                    val contourEnd = contour.last()

                    when {
                        distance(ringStart, contourEnd) <
                                connectionTolerance -> {

                            foundIndex = index
                            reverse = false
                        }

                        distance(ringStart, contourStart) <
                                connectionTolerance -> {

                            foundIndex = index
                            reverse = true
                        }
                    }

                    if (foundIndex != -1) {
                        return@forEachIndexed
                    }
                }

                if (foundIndex != -1) {

                    val next = remaining.removeAt(foundIndex)

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

        // Cerramos el anillo.
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

@Composable
fun LoadInsideMap(
    uiState: ChatUiState,
    geoJson: String,
    userLocation: LocationUiState,
    itemSelected: (String) -> Unit,
    places: List<ElementsFoundUiState>
) {
    val placesSource = rememberGeoJsonSource(
        data = GeoJsonData.JsonString(geoJson)
    )
    val userLocationSource = rememberGeoJsonSource(
        data = GeoJsonData.JsonString(
            userLocationToGeoJson(
                latitude = userLocation.latitude.roundTo4Decimals(),
                longitude = userLocation.longitude.roundTo4Decimals()
            )
        )
    )

    val geometry = calculatePlacesRadiusGeometry(
        places = places,
        radiusMeters = 500.0
    )

    val closedContours = closeContours(
        geometry.contours
    )

    val radiusGeoJson = radiusGeometryToLineGeoJson(
        geometry = geometry
    )

    val radiusSource = rememberGeoJsonSource(
        data = GeoJsonData.JsonString(radiusGeoJson)
    )

    val outsideSource = rememberGeoJsonSource(
        data = GeoJsonData.JsonString(
            createOutsideAreaGeoJson(
                contours = closedContours,
                places = places
            )
        )
    )

    FillLayer(
        id = "outside-area",
        source = outsideSource,
        color = const(Color.Black),
        opacity = const(0.45f)
    )

    LineLayer(
        id = "search-radius",
        source = radiusSource,
        color = const(Color.Blue),
        width = const(3.dp)
    )

    SymbolLayer(
        id = "place",
        source = placesSource,
        iconImage = switch(
            feature["type"].asString(),

            case(
                label = "restaurant",
                output = image(
                    rememberVectorPainter(Icons.Default.Restaurant)
                )
            ),

            case(
                label = "cafe",
                output = image(
                    rememberVectorPainter(Icons.Default.LocalCafe)
                )
            ),

            case(
                label = "bar",
                output = image(
                    rememberVectorPainter(Icons.Default.LocalBar)
                )
            ),

            case(
                label = "fuel",
                output = image(
                    rememberVectorPainter(Icons.Default.LocalGasStation)
                )
            ),

            case(
                label = "park",
                output = image(
                    rememberVectorPainter(Icons.Default.Park)
                )
            ),

            case(
                label = "museum",
                output = image(
                    rememberVectorPainter(Icons.Default.Museum)
                )
            ),

            case(
                label = "meal",
                output = image(
                    rememberVectorPainter(Icons.Default.Fastfood)
                )
            ),
            case(
                label = "ice",
                output = image(
                    rememberVectorPainter(Icons.Default.Icecream)
                )
            ),
            fallback = image(
                rememberVectorPainter(Icons.Default.LocationOn)
            )
        ),
        iconSize = const(1f),
        iconAnchor = const(SymbolAnchor.Bottom),
        iconAllowOverlap = const(true),
        onClick = { features ->
            val feature = features.firstOrNull()
            feature?.getStringProperty("id")?.let { itemSelected(it) }
            ClickResult.Consume
        }
    )

    SymbolLayer(
        id = "user-location",
        source = userLocationSource,
        iconImage = image(
            rememberVectorPainter(if (uiState.outOfLimits) Icons.Default.LocationDisabled else Icons.AutoMirrored.Filled.DirectionsWalk)
        ),
        iconSize = const(0.8f),
        iconAnchor = const(SymbolAnchor.Bottom),
        iconAllowOverlap = const(true)
    )
}

private fun calculatePlacesRadiusGeometry(
    places: List<ElementsFoundUiState>,
    radiusMeters: Double = 500.0
): RadiusGeometry {

    if (places.isEmpty()) {
        return RadiusGeometry(emptyList())
    }

    val earthRadius = 6_371_000.0

    val referenceLatitude =
        places.map { it.latitude }.average()

    val referenceLongitude =
        places.map { it.longitude }.average()

    val referenceLatRadians =
        referenceLatitude * kotlin.math.PI / 180.0

    fun toX(longitude: Double): Double {
        return (longitude - referenceLongitude) *
                kotlin.math.PI / 180.0 *
                earthRadius *
                kotlin.math.cos(referenceLatRadians)
    }

    fun toY(latitude: Double): Double {
        return (latitude - referenceLatitude) *
                kotlin.math.PI / 180.0 *
                earthRadius
    }

    fun toLongitude(x: Double): Double {
        return referenceLongitude +
                x /
                (earthRadius * kotlin.math.cos(referenceLatRadians)) *
                180.0 / kotlin.math.PI
    }

    fun toLatitude(y: Double): Double {
        return referenceLatitude +
                y / earthRadius *
                180.0 / kotlin.math.PI
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

    val fullCircle = 2.0 * kotlin.math.PI

    val contours = mutableListOf<List<Pair<Double, Double>>>()

    centers.forEachIndexed { index, center ->

        val coveredIntervals = mutableListOf<Interval>()

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
                    start = last.start,
                    end = maxOf(
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
                    start = current,
                    end = interval.start
                )
            }

            current = maxOf(
                current,
                interval.end
            )
        }

        if (current < fullCircle) {

            visibleIntervals += Interval(
                start = current,
                end = fullCircle
            )
        }

        if (mergedIntervals.isEmpty()) {

            visibleIntervals.clear()

            visibleIntervals += Interval(
                start = 0.0,
                end = fullCircle
            )
        }

        visibleIntervals.forEach { interval ->

            val angleLength =
                interval.end - interval.start

            val segments = maxOf(
                8,
                (angleLength / (kotlin.math.PI / 36.0))
                    .toInt()
            )

            val coordinates =
                (0..segments).map { i ->

                    val progress =
                        i.toDouble() / segments

                    val angle =
                        interval.start +
                                angleLength * progress

                    val x =
                        center.x +
                                radiusMeters *
                                kotlin.math.cos(angle)

                    val y =
                        center.y +
                                radiusMeters *
                                kotlin.math.sin(angle)

                    Pair(
                        toLongitude(x),
                        toLatitude(y)
                    )
                }

            if (coordinates.size >= 2) {
                contours += coordinates
            }
        }
    }

    return RadiusGeometry(
        contours = contours
    )
}

fun radiusGeometryToLineGeoJson(
    geometry: RadiusGeometry
): String {

    val features = geometry.contours.joinToString(",") { contour ->

        val coordinates =
            contour.joinToString(",") { (longitude, latitude) ->
                "[$longitude,$latitude]"
            }

        """
        {
            "type": "Feature",
            "properties": {},
            "geometry": {
                "type": "LineString",
                "coordinates": [$coordinates]
            }
        }
        """.trimIndent()
    }

    return """
        {
            "type": "FeatureCollection",
            "features": [$features]
        }
    """.trimIndent()
}

fun createOutsideAreaGeoJson(
    contours: List<List<Pair<Double, Double>>>,
    places: List<ElementsFoundUiState>
): String {

    if (contours.isEmpty() || places.isEmpty()) {
        return """
            {
                "type": "FeatureCollection",
                "features": []
            }
        """.trimIndent()
    }

    val centerLatitude =
        places.map { it.latitude }.average()

    val centerLongitude =
        places.map { it.longitude }.average()

    /*
     * Creamos un rectángulo suficientemente grande
     * para cubrir toda la zona visible.
     */
    val delta = 1.0

    val outerRing = """
        [
            [
                ${centerLongitude - delta},
                ${centerLatitude - delta}
            ],
            [
                ${centerLongitude + delta},
                ${centerLatitude - delta}
            ],
            [
                ${centerLongitude + delta},
                ${centerLatitude + delta}
            ],
            [
                ${centerLongitude - delta},
                ${centerLatitude + delta}
            ],
            [
                ${centerLongitude - delta},
                ${centerLatitude - delta}
            ]
        ]
    """.trimIndent()

    /*
     * Cada contour es un agujero del Polygon.
     */
    val holes = contours.joinToString(",") { contour ->

        contour.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ","
        ) { (longitude, latitude) ->
            "[$longitude,$latitude]"
        }
    }

    return """
        {
            "type": "Feature",
            "properties": {},
            "geometry": {
                "type": "Polygon",
                "coordinates": [
                    $outerRing,
                    $holes
                ]
            }
        }
    """.trimIndent()
}


fun normalizePlaceType(type: String): String {
    return when {
        type.contains("restaurant", ignoreCase = true) -> "restaurant"
        type.contains("meal", ignoreCase = true) -> "meal"
        type.contains("ice", ignoreCase = true) -> "ice"
        type.contains("cafe", ignoreCase = true) -> "cafe"
        type.contains("bar", ignoreCase = true) -> "bar"
        type.contains("pub", ignoreCase = true) -> "bar"
        type.contains("fast_food", ignoreCase = true) -> "fast_food"
        type.contains("bakery", ignoreCase = true) -> "bakery"
        type.contains("supermarket", ignoreCase = true) -> "supermarket"
        type.contains("pharmacy", ignoreCase = true) -> "pharmacy"
        type.contains("hospital", ignoreCase = true) -> "hospital"
        type.contains("fuel", ignoreCase = true) -> "fuel"
        else -> "place"
    }
}

private fun placesToGeoJson(
    places: List<ElementsFoundUiState>
): String {
    return """
        {
          "type": "FeatureCollection",
          "features": [
            ${
        places.joinToString(",") { place ->
            """
                    {
                      "type": "Feature",
                      "properties": {
                        "id": ${Json.encodeToString(place.id)},
                        "name": ${Json.encodeToString(place.name)},
                        "type": ${Json.encodeToString(normalizePlaceType(place.type ?: ""))}
                      },
                      "geometry": {
                        "type": "Point",
                        "coordinates": [
                          ${place.longitude},
                          ${place.latitude}
                        ]
                      }
                    }
                    """.trimIndent()
        }
    }
          ]
        }
    """.trimIndent()
}

private fun userLocationToGeoJson(
    latitude: Double,
    longitude: Double
): String {
    return """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "properties": {
                "type": "user"
              },
              "geometry": {
                "type": "Point",
                "coordinates": [
                  $longitude,
                  $latitude
                ]
              }
            }
          ]
        }
    """.trimIndent()
}