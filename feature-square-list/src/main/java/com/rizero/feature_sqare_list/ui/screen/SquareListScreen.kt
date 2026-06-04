package com.rizero.feature_sqare_list.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.rizero.feature_sqare_list.R
import com.rizero.feature_sqare_list.component.MockSquareListComponent
import com.rizero.feature_sqare_list.component.SquareListComponent
import com.rizero.feature_sqare_list.ui.component.SquareListItem
import com.rizero.feature_sqare_list.ui.component.TwoSegmentAnimatedSwitch
import com.rizero.feature_sqare_list.ui.component.TwoSegmentAnimatedSwitchPosition
import com.rizero.shared_ui.AppColors
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.spatialk.geojson.Position

enum class SelectedSquareDisplay{
    LIST,
    MAP,
}

@Composable
fun SquareListScreen(squareListComponent: SquareListComponent, synchronized: Boolean = true){
    var selectedSquareDisplay by remember { mutableStateOf(SelectedSquareDisplay.LIST) }
    var nearestSquaresExpanded by remember { mutableStateOf(false) }
    var allSquaresExpanded by remember { mutableStateOf(false) }
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
                    text = "Путевой лист №123 от 27.05.2026",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            HorizontalDivider()
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
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = {
                            nearestSquaresExpanded = !nearestSquaresExpanded
                        })
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Ближайшие площадки",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Image(
                        imageVector = if (nearestSquaresExpanded)
                            ImageVector.vectorResource(R.drawable.keyboard_arrow_up)
                        else
                            ImageVector.vectorResource(R.drawable.keyboard_arrow_down),
                        contentDescription = "Показать ближайшие",
                        modifier = Modifier.size(30.dp)
                    )
                }
                HorizontalDivider()
                AnimatedVisibility(
                    visible = nearestSquaresExpanded,
                ) {
                    LazyColumn() {
                        item {
                            SquareListItem(1,true,"ул. Ленина 10", 250){
                                squareListComponent.openGarbageSite()
                            }
                            HorizontalDivider()
                        }
                        item {
                            SquareListItem(2,false,"ул. Крынина 12", 560){
                                squareListComponent.openGarbageSite()
                            }
                            HorizontalDivider()
                        }
                        item {
                            SquareListItem(3,true,"ул. Ломоносова 23", 1100){
                                squareListComponent.openGarbageSite()
                            }
                            HorizontalDivider()
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                allSquaresExpanded = !allSquaresExpanded
                            }
                        )
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Все площадки (7)",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Image(
                        imageVector = if (allSquaresExpanded)
                            ImageVector.vectorResource(R.drawable.keyboard_arrow_up)
                        else
                            ImageVector.vectorResource(R.drawable.keyboard_arrow_down),
                        contentDescription = "Показать все площадки",
                        modifier = Modifier.size(30.dp)
                    )
                }
                HorizontalDivider()

                AnimatedVisibility(
                    visible = allSquaresExpanded,
                ) {
                    LazyColumn() {
                        item {
                            SquareListItem(1,true,"ул. Ленина 10", 250){
                                squareListComponent.openGarbageSite()
                            }
                            HorizontalDivider()
                        }
                        item {
                            SquareListItem(2,false,"ул. Крынина 12", 560){
                                squareListComponent.openGarbageSite()
                            }
                            HorizontalDivider()
                        }
                        item {
                            SquareListItem(3,true,"ул. Ломоносова 23", 1100){
                                squareListComponent.openGarbageSite()
                            }
                            HorizontalDivider()
                        }
                        item {
                            SquareListItem(4,false,"ул. Ломоносова 45", 1300){
                                squareListComponent.openGarbageSite()
                            }
                            HorizontalDivider()
                        }
                        item {
                            SquareListItem(5,false,"ул. Ломоносова 117", 1800){
                                squareListComponent.openGarbageSite()
                            }
                            HorizontalDivider()
                        }
                    }
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
                )
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

@Composable
@Preview(showBackground = true, )
fun LoadedSynchronizedSquareListScreenPreview(){
    SquareListScreen(squareListComponent = MockSquareListComponent(),true)
}

@Composable
@Preview(showBackground = true, )
fun LoadedUnsynchronizedSquareListScreenPreview(){
    SquareListScreen(squareListComponent = MockSquareListComponent(),false)
}
