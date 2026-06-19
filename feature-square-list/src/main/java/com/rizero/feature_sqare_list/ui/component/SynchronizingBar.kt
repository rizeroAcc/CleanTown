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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_sqare_list.R
import com.rizero.feature_sqare_list.store.GarbageSiteListStore
import com.rizero.shared_ui.AppColors
import com.rizero.shared_ui.formatDdMmYyHm

@Composable
fun SynchronizingBar(loadedWaybill : GarbageSiteListStore.State.WaybillState.Loaded){

    HorizontalDivider()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(AppColors.lightBackgroundColor)
            .height(60.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = if (loadedWaybill.synchronized)
                ImageVector.vectorResource(R.drawable.checkmarkx)
            else
                ImageVector.vectorResource(R.drawable.update_clock),
            contentDescription = if (loadedWaybill.synchronized)
                "Синхронизировано"
            else
                "Ожидает синхронизации",
            tint = if (loadedWaybill.synchronized) Color.Green else Color.Yellow,
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
                text = if (loadedWaybill.synchronized)
                    "Синхронизировано"
                else
                    "Ожидает синхронизации",
                color = Color.White,
                fontSize = 16.sp
            )
            Text(
                text = if (loadedWaybill.synchronized)
                    loadedWaybill.loadTime.formatDdMmYyHm()
                else
                    "Время последнего обновления:\n${loadedWaybill.loadTime.formatDdMmYyHm()}",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
    HorizontalDivider()

}

