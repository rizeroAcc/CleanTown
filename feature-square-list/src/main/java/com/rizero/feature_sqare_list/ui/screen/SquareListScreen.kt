package com.rizero.feature_sqare_list.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Waybill
import com.rizero.feature_sqare_list.component.MockSquareListComponent
import com.rizero.feature_sqare_list.component.SquareListComponent
import com.rizero.feature_sqare_list.store.GarbageSiteListStore
import com.rizero.feature_sqare_list.ui.component.SynchronizingBar
import com.rizero.feature_sqare_list.ui.component.TwoSegmentAnimatedSwitch
import com.rizero.feature_sqare_list.ui.component.TwoSegmentAnimatedSwitchPosition
import com.rizero.feature_sqare_list.ui.component.WaybillSitesList
import com.rizero.shared_ui.AppColors
import kotlinx.coroutines.flow.MutableStateFlow
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.spatialk.geojson.Position
import java.time.LocalDateTime
import java.util.UUID

enum class SelectedSquareDisplay{
    LIST,
    MAP,
}

@Composable
fun SquareListScreen(squareListComponent: SquareListComponent){
    val state by squareListComponent.state.collectAsState()
    var selectedSquareDisplay by rememberSaveable { mutableStateOf(SelectedSquareDisplay.LIST) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(AppColors.defaultBackgroundColor)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Площадки",
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = when(val state = state.waybillState){
                        is GarbageSiteListStore.State.WaybillState.Loaded -> "Путевой лист от ${state.waybill.date}"
                        GarbageSiteListStore.State.WaybillState.Loading -> "Путевой лист загружается"
                        is GarbageSiteListStore.State.WaybillState.LoadingError -> "Ошибка загрузки путевого листа"
                        GarbageSiteListStore.State.WaybillState.WaybillNotFound -> "Не найден текущий путевой лист"
                    },
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (state.currentLocationState is GarbageSiteListStore.State.LocationState.LocationReceived) {
                Text(
                    text = "Координаты: ${(state.currentLocationState as GarbageSiteListStore.State.LocationState.LocationReceived).location.latitude} , ${(state.currentLocationState as GarbageSiteListStore.State.LocationState.LocationReceived).location.longitude}",
                    color = Color.White
                    )
            }

            when(val waybillState = state.waybillState){
                GarbageSiteListStore.State.WaybillState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
                is GarbageSiteListStore.State.WaybillState.LoadingError -> {

                }
                GarbageSiteListStore.State.WaybillState.WaybillNotFound -> {

                }
                is GarbageSiteListStore.State.WaybillState.Loaded -> {
                    SynchronizingBar(waybillState)
                    TwoSegmentAnimatedSwitch(
                        selected = if (selectedSquareDisplay == SelectedSquareDisplay.LIST)
                            TwoSegmentAnimatedSwitchPosition.LEFT
                        else
                            TwoSegmentAnimatedSwitchPosition.RIGHT,
                        leftContent = {
                            Text(
                                text = "Список",
                                color = if (selectedSquareDisplay == SelectedSquareDisplay.LIST)
                                    Color.Green
                                else
                                    Color.White,
                            )
                        },
                        rightContent = {
                            Text(
                                text = "На карте",
                                color = if (selectedSquareDisplay == SelectedSquareDisplay.MAP)
                                    Color.Green
                                else
                                    Color.White,
                            )
                        },
                        onLeftClick = {
                            selectedSquareDisplay = SelectedSquareDisplay.LIST
                        },
                        onRightClick = {
                            selectedSquareDisplay = SelectedSquareDisplay.MAP
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                    if (selectedSquareDisplay == SelectedSquareDisplay.LIST){
                        WaybillSitesList(
                            //TODO Оптимизация (вынести рассчет в стор и хранить ближайшие в стейте)
                            nearestGarbageSiteList = if (state.distanceToGarbageSitesCalculated) {
                                waybillState.waybill.garbageSites.sortedBy { it.distanceTo!! }.take(3)
                            } else null,
                            garbageSiteList = waybillState.waybill.garbageSites,
                            allSquaresExpanded = true,
                            nearestSquaresExpanded = false
                        ){ garbageSite ->
                            squareListComponent.openGarbageSite(garbageSite)
                        }
                    } else {
                        val cameraState = rememberCameraState(CameraPosition(
                            zoom = 16.0,
                            target = Position(latitude = 51.657063, longitude = 39.205146)
                        ))
                        val styleState = rememberStyleState()
                        MaplibreMap(
                            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
                            cameraState = cameraState,
                            styleState = styleState,
                            modifier = Modifier.padding(2.dp)
                        ){

                        }
                    }
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.buttonBackgroundColor
                        ),
                        onClick = {
                            squareListComponent.finishShift()
                        },
                        modifier = Modifier
                            .padding(vertical = 24.dp)
                            .fillMaxWidth(0.7f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Завершить смену"
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, )
fun LoadedSynchronizedSquareListScreenPreview(){
    SquareListScreen(squareListComponent = MockSquareListComponent(
        mockState = MutableStateFlow(GarbageSiteListStore.State(
                waybillState = GarbageSiteListStore.State.WaybillState.Loaded(
                    waybill = Waybill(
                        date = "27.05.2026",
                        driver = "Вася пупкин",
                        garbageSites = listOf(
                            GarbageSite(
                                id = UUID.randomUUID().toString(),
                                address = "Ломоносова 10",
                                longitude = 51.252,
                                latitude = 32.245,
                                distanceTo = 100,
                                report = null,
                            )
                        ),
                        id = UUID.randomUUID().toString()
                    ),
                    loadTime = LocalDateTime.now(),
                    synchronized = true,
            ),
            currentLocationState = GarbageSiteListStore.State.LocationState.Loading
        ))
    ))
}

@Composable
@Preview(showBackground = true, )
fun LoadedUnsynchronizedSquareListScreenPreview(){
    Box(modifier = Modifier
        .padding(top = 20.dp)
        .fillMaxSize()){
        SquareListScreen(
            squareListComponent = MockSquareListComponent(
                mockState = MutableStateFlow(GarbageSiteListStore.State(
                    waybillState = GarbageSiteListStore.State.WaybillState.Loaded(
                        waybill = Waybill(
                            date = "27.05.2026",
                            driver = "Вася пупкин",
                            garbageSites = listOf(
                                GarbageSite(
                                    id = UUID.randomUUID().toString(),
                                    address = "Ломоносова 10",
                                    longitude = 51.252,
                                    latitude = 32.245,
                                    distanceTo = 100,
                                    report = null,
                                )
                            ),
                            id = UUID.randomUUID().toString()
                        ),
                        loadTime = LocalDateTime.now(),
                        synchronized = false
                    ),
                    currentLocationState = GarbageSiteListStore.State.LocationState.Loading
                )
                )
            ))
    }

}
