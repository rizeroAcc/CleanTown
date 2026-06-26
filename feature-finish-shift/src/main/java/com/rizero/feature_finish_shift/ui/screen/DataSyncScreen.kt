package com.rizero.feature_finish_shift.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_finish_shift.R
import com.rizero.feature_finish_shift.component.DataSyncComponent
import com.rizero.feature_finish_shift.component.MockDataSyncComponent
import com.rizero.feature_finish_shift.store.SyncDataStore
import com.rizero.shared_ui.AppColors

@Composable
fun DataSyncScreen(dataSyncComponent: DataSyncComponent){
    val state by dataSyncComponent.state.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = AppColors.defaultBackgroundColor)
            .fillMaxSize()
    ) {
        Text(
            text = "Синхронизация данных",
            color = AppColors.defaultTextColor,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 12.dp)
        )
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        when(val syncState = state.syncState){
            SyncDataStore.State.SyncState.Init -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ){
                    Text(
                        text = "Загрузка...",
                        fontSize = 20.sp,
                        color = AppColors.defaultTextColor
                    )
                }
            }
            is SyncDataStore.State.SyncState.Sync -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    CircularProgressWithPercentage(
                        progress = syncState.syncCount.toFloat()/syncState.total,
                        modifier = Modifier
                            .padding(12.dp)
                            .sizeIn(
                                150.dp,
                                150.dp,
                                250.dp,
                                250.dp
                            )
                    )
                    Text(
                        text = "Отправка данных на сервер",
                        color = AppColors.defaultTextColor,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Не отключайте интернет\nи не закрывайте приложение",
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
                    HorizontalDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.update_clock),
                            contentDescription = "Загрузка",
                            tint = Color.Yellow,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(36.dp)
                        )
                        Text(
                            text = "Отпрвлено площадок:",
                            color = Color.LightGray,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 12.dp).weight(1f)
                        )
                        Text(
                            text = "${syncState.syncCount} из ${syncState.total}",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                    HorizontalDivider()
                }
            }
            SyncDataStore.State.SyncState.SyncFinished -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ){
                    Text(
                        text = "Синхронизация завершена",
                        fontSize = 20.sp,
                        color = AppColors.defaultTextColor
                    )
                }
            }
        }

        Button(
            enabled = state.syncState is SyncDataStore.State.SyncState.SyncFinished,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = AppColors.lightBackgroundColor,
                containerColor = AppColors.buttonBackgroundColor
            ),
            onClick = {

            },
            modifier = Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text(
                text = "Завершить работу"
            )
        }
    }
}

@Composable
fun CircularProgressWithPercentage(
    progress: Float,
    modifier: Modifier = Modifier.size(180.dp),
    color: Color = Color(0xFF00FF88),
    backgroundColor: Color = Color(0xFF2A2F38),
    strokeWidth: Dp = 12.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Фоновая окружность
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor,
            strokeWidth = strokeWidth,
            trackColor = Color.Transparent
        )

        // Прогресс
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = strokeWidth,
            trackColor = Color.Transparent
        )

        // Процент в центре
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
@Preview
fun DataSyncScreenPreview(){
    DataSyncScreen(MockDataSyncComponent())
}