package com.rizero.feature_sqare_list.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_sqare_list.R
import com.rizero.feature_sqare_list.store.GarbageSiteListStore
import com.rizero.shared_ui.AppColors
import com.rizero.shared_ui.formatDdMmYyHm

@Composable
fun SynchronizingBar(
    waybillState : GarbageSiteListStore.State.WaybillState,
    onRefreshClick : () -> Unit
){
    if (waybillState is GarbageSiteListStore.State.WaybillState.Error && waybillState.cachedWaybill == null) return
    if (waybillState is GarbageSiteListStore.State.WaybillState.InitialLoading || waybillState is GarbageSiteListStore.State.WaybillState.InitialLoading) return

    HorizontalDivider()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(AppColors.lightBackgroundColor)
            .height(60.dp)
            .fillMaxWidth()
    ) {
        when(waybillState){
            is GarbageSiteListStore.State.WaybillState.Error -> {
                Icon(
                    imageVector = if (waybillState.isRefreshing)
                        ImageVector.vectorResource(R.drawable.update_clock)
                    else
                        ImageVector.vectorResource(R.drawable.fail_cross),
                    contentDescription = "Синхронизация",
                    tint = if (waybillState.isRefreshing)
                        Color.Yellow
                    else
                        Color.Red,
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
                        text = if(waybillState.isRefreshing) "Синхронизация..." else "Ошибка синхронизации!",
                        color = Color.White,
                        lineHeight = 16.sp,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = "Время последнего обновления:\n${waybillState.cachedWaybill!!.updateTime.formatDdMmYyHm()}",
                        color = Color.White,
                        lineHeight = 14.sp,
                        fontSize = 14.sp
                    )
                }
            }
            is GarbageSiteListStore.State.WaybillState.Loaded -> {
                Icon(
                    imageVector = if (waybillState.isRefreshing)
                        ImageVector.vectorResource(R.drawable.update_clock)
                    else
                        ImageVector.vectorResource(R.drawable.checkmarkx),
                    contentDescription = if(waybillState.isRefreshing) "Синхронизация..." else "Синхронизированно",
                    tint = if (waybillState.isRefreshing)
                        Color.Yellow
                    else
                        Color.Green,
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
                        text = if(waybillState.isRefreshing) "Синхронизация..." else "Синхронизированно",
                        color = Color.White,
                        lineHeight = 16.sp,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Время последнего обновления:\n${waybillState.waybill.updateTime.formatDdMmYyHm()}",
                        color = Color.White,
                        lineHeight = 16.sp,
                        fontSize = 14.sp,
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { onRefreshClick() },
                modifier = Modifier.padding(end = 8.dp)
            ){
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.refresh),
                    contentDescription = "Обновить путевой лист",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }




    }
    HorizontalDivider()

}

