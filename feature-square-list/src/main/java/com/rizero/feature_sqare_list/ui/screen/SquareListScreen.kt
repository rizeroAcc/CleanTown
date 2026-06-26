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
import com.rizero.feature_sqare_list.ui.component.GarbageSiteMap
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
import org.maplibre.compose.style.BaseStyle.*
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
                        is GarbageSiteListStore.State.WaybillState.Error -> "Ошибка загрузки путевого листа: ${state.message}"
                        is GarbageSiteListStore.State.WaybillState.InitialLoading -> "Путевой лист загружается ${
                            if (state.dataSource == GarbageSiteListStore.State.DataSource.CACHE) 
                                "из памяти"
                            else
                                "с сервера"
                        }"
                        is GarbageSiteListStore.State.WaybillState.Loaded -> "Путевой лист от ${state.waybill.date}"
                    },
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            when(val waybillState = state.waybillState){
                is GarbageSiteListStore.State.WaybillState.InitialLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
                else -> {
                    if (waybillState is GarbageSiteListStore.State.WaybillState.Error && waybillState.cachedWaybill == null){
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Button(
                                onClick = {

                                }
                            ) {
                                Text("Повторить загрузку")
                            }
                        }
                    }else{
                        val waybill = when(waybillState){
                            is GarbageSiteListStore.State.WaybillState.Error -> waybillState.cachedWaybill!!
                            is GarbageSiteListStore.State.WaybillState.Loaded -> waybillState.waybill
                        }
                        SynchronizingBar(waybillState){
                            squareListComponent.fetchWaybill()
                        }
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
                                nearestGarbageSiteList = when(waybillState){
                                    is GarbageSiteListStore.State.WaybillState.Error -> waybillState.nearestGarbageSites
                                    is GarbageSiteListStore.State.WaybillState.Loaded -> waybillState.nearestGarbageSites
                                },
                                garbageSiteList = waybill.garbageSites,
                                allSquaresExpanded = true,
                                nearestSquaresExpanded = false
                            ){ garbageSite ->
                                squareListComponent.openGarbageSite(garbageSite)
                            }
                        } else {
                            GarbageSiteMap(
                                garbageSites = waybill.garbageSites,
                                locationState = state.currentLocationState,
                                onGarbageSiteSelected = { garbageSite->
                                    squareListComponent.openGarbageSite(garbageSite = garbageSite)
                                },
                                modifier = Modifier.padding(2.dp)
                            )
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
                        updateTime = LocalDateTime.now(),
                        id = UUID.randomUUID().toString()
                    ),

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
                            updateTime = LocalDateTime.now(),
                            id = UUID.randomUUID().toString()
                        ),
                    ),
                    currentLocationState = GarbageSiteListStore.State.LocationState.Loading
                )
                )
            ))
    }

}
