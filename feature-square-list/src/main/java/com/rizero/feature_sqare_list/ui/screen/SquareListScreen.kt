package com.rizero.feature_sqare_list.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Waybill
import com.rizero.feature_sqare_list.R
import com.rizero.feature_sqare_list.component.MockSquareListComponent
import com.rizero.feature_sqare_list.component.SquareListComponent
import com.rizero.feature_sqare_list.store.GarbageSiteListStore
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
import java.util.UUID

enum class SelectedSquareDisplay{
    LIST,
    MAP,
}

@Composable
fun SquareListScreen(squareListComponent: SquareListComponent, synchronized: Boolean = true){
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
                    text = if (state.waybill == null)
                        "Загрузка путевого листа"
                    else
                        "Путевой лист от ${state.waybill!!.date}",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            HorizontalDivider()
            if (state.currentLocation is GarbageSiteListStore.State.LocationState.LocationRecieved) {
                Text(
                    text = "Координаты: ${(state.currentLocation as GarbageSiteListStore.State.LocationState.LocationRecieved).location.latitude} , ${(state.currentLocation as GarbageSiteListStore.State.LocationState.LocationRecieved).location.longitude}",
                    color = Color.White
                    )
            }
            if (state.waybill == null) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(AppColors.lightBackgroundColor)
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (synchronized)
                            ImageVector.vectorResource(R.drawable.checkmarkx)
                        else
                            ImageVector.vectorResource(R.drawable.update_clock),
                        contentDescription = if (synchronized)
                            "Синхронизировано"
                        else
                            "Ожидает синхронизации",
                        tint = if (synchronized) Color.Green else Color.Yellow,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(48.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = if (synchronized)
                                "Синхронизировано"
                            else
                                "Ожидает синхронизации",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (synchronized)
                                "27.05.2026 8:30"
                            else
                                "Время последнего обновления:\n27.05.2026 8:30",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
                HorizontalDivider()
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
                        garbageSiteList = state.waybill?.garbageSites?: emptyList(),
                        allSquaresExpanded = true,
                        nearestSquaresExpanded = true
                    )
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
                    )
                }
            }
            Button(
                enabled = state.waybill!=null,
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

@Composable
@Preview(showBackground = true, )
fun LoadedSynchronizedSquareListScreenPreview(){
    SquareListScreen(squareListComponent = MockSquareListComponent(
        mockState = MutableStateFlow(GarbageSiteListStore.State(
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
            currentLocation = GarbageSiteListStore.State.LocationState.Loading
        ))
    ),true)
}

@Composable
@Preview(showBackground = true, )
fun LoadedUnsynchronizedSquareListScreenPreview(){
    Box(modifier = Modifier
        .padding(top = 100.dp)
        .fillMaxSize()){
        SquareListScreen(
            squareListComponent = MockSquareListComponent(
                mockState = MutableStateFlow(GarbageSiteListStore.State(
                    waybill = null,
                    currentLocation = GarbageSiteListStore.State.LocationState.Loading
                )
                )
            ), false)
    }

}
