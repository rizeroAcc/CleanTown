package com.rizero.feature_finish_shift.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.feature_finish_shift.component.FinishShiftComponent
import com.rizero.feature_finish_shift.component.MockFinishShiftComponent
import com.rizero.feature_finish_shift.store.FinishShiftStore
import com.rizero.feature_finish_shift.ui.component.UnservedGarbageSiteListItem
import com.rizero.feature_uncollect_reason.screen.UncollectedReasonDialog
import com.rizero.shared_ui.AppColors

@Composable
fun FinishShiftScreen(finishShiftComponent: FinishShiftComponent){
    val dialog = finishShiftComponent.uncollectedReasonDialog.subscribeAsState()
    val state by finishShiftComponent.state.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = AppColors.defaultBackgroundColor)
            .fillMaxSize()
    ) {
        Text(
            text = "Завершение смены",
            color = AppColors.defaultTextColor,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 12.dp)
        )
        Text(
            text = "Выбор причины невывоза",
            color = AppColors.defaultTextColor,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 4.dp)
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 6.dp)
        )
        if(state.uncollectedGarbageSites is FinishShiftStore.State.UncollectedGarbageSites.Loaded){
            val uncollectedSites = (state.uncollectedGarbageSites as FinishShiftStore.State.UncollectedGarbageSites.Loaded).garbageSites
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Невывезенные площадки:",
                    color = AppColors.defaultTextColor,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = 12.dp)
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(top = 6.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(uncollectedSites){ index,item->
                    UnservedGarbageSiteListItem(
                        index + 1,
                        item.address,
                        state.uncollectedReason?.name ?: "Не указака"
                    ) {}
                    HorizontalDivider()
                }
            }
        }else{
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "Загрузка...",
                    fontSize = 20.sp,
                    color = AppColors.defaultTextColor,
                )
            }
        }

        Button(
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.buttonBackgroundColor
            ),
            onClick = {
                finishShiftComponent.selectUncollectedReason()
            },
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text(
                text = "Выбрать причину невывоза"
            )
        }
        Button(
            enabled = state.uncollectedReason != null,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = AppColors.lightBackgroundColor,
                containerColor = AppColors.buttonBackgroundColor
            ),
            onClick = {
                finishShiftComponent.writeUncollectedReason()
            },
            modifier = Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text(
                text = "Записать причины невывоза"
            )
        }
    }
    dialog.value.child?.let { child->
        Dialog(
            onDismissRequest = {
                
            }
        ) {
            UncollectedReasonDialog(child.instance)
        }
    }
}

@Composable
@Preview
fun FinishShiftScreenPreview(){
    FinishShiftScreen(MockFinishShiftComponent())
}