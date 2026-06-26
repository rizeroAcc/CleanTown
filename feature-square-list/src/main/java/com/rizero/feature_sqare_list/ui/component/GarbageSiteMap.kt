package com.rizero.feature_sqare_list.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.rizero.core_data.model.GarbageSite
import com.rizero.feature_sqare_list.R
import com.rizero.feature_sqare_list.store.GarbageSiteListStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import org.maplibre.android.style.expressions.Expression.get
import org.maplibre.android.style.expressions.Expression.image
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.zoom
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.material3.CompassButton
import org.maplibre.compose.material3.DisappearingCompassButton
import org.maplibre.compose.material3.DisappearingScaleBar
import org.maplibre.compose.material3.ExpandingAttributionButton
import org.maplibre.compose.material3.ScaleBar
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle.Uri
import org.maplibre.compose.style.StyleState
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

@Composable
fun GarbageSiteMap(
    locationState: GarbageSiteListStore.State.LocationState,
    garbageSites: List<GarbageSite>,
    onGarbageSiteSelected : (GarbageSite)->Unit,
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(CameraPosition(
        zoom = 16.0,
        target = Position(latitude = 51.657063, longitude = 39.205146)
        )
    ),
    styleState: StyleState = rememberStyleState(),
){
    val scope = rememberCoroutineScope { Dispatchers.Main }
    Box(modifier = modifier){
        MaplibreMap(
            baseStyle = Uri("https://tiles.openfreemap.org/styles/liberty"),
            cameraState = cameraState,
            styleState = styleState,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
        ){
            val markerSource = rememberGeoJsonSource(
                data = GeoJsonData.Features(
                    FeatureCollection(
                        features = garbageSites.map { site ->
                            Feature(
                                geometry = Point(
                                    Position(site.longitude, site.latitude)  // Важно: lng, lat
                                ),
                                properties = site
                            )
                        }
                    )
                )
            )
            SymbolLayer(
                id = "garbage-sites-layer",
                source = markerSource,
                iconImage = image(value = painterResource(R.drawable.place_mark), size = DpSize(36.dp,36.dp)),
                iconAnchor = const(SymbolAnchor.Bottom),
                iconColor = const(Color.Red),
                iconAllowOverlap = const(true),
                onClick = { features ->
                    onGarbageSiteSelected(Json.decodeFromJsonElement(features[0].properties as JsonElement))
                    ClickResult.Consume
                }
            )
        }
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            DisappearingScaleBar(
                metersPerDp = cameraState.metersPerDpAtTarget,
                zoom = cameraState.position.zoom,
                modifier = Modifier.align(Alignment.TopStart),
            )
            CompassButton(
                onClick = {
                    if (locationState is GarbageSiteListStore.State.LocationState.LocationReceived){
                        scope.launch {
                            cameraState.animateTo(finalPosition = CameraPosition(
                                zoom = cameraState.position.zoom,
                                target = Position(latitude = locationState.location.latitude, longitude = locationState.location.longitude)
                            ))
                        }
                    }
                },
                cameraState = cameraState,
                modifier = Modifier.align(Alignment.TopEnd)
            )
            ExpandingAttributionButton(
                cameraState = cameraState,
                styleState = styleState,
                modifier = Modifier.align(Alignment.BottomEnd),
                contentAlignment = Alignment.BottomEnd,
            )
        }
    }
}