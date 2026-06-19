package com.rizero.feature_finish_shift.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.feature_finish_shift.component.FinishShiftComponent
import com.rizero.feature_finish_shift.component.MockFinishShiftComponent
import com.rizero.feature_finish_shift.ui.component.UnservedGarbageSiteListItem
import com.rizero.feature_uncollect_reason.screen.UncollectedReasonDialog
import com.rizero.shared_ui.AppColors

@Composable
fun FinishShiftScreen(finishShiftComponent: FinishShiftComponent){
    val dialog = finishShiftComponent.uncollectedReasonDialog.subscribeAsState()
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
            item {
                UnservedGarbageSiteListItem(
                    1,
                    "Ломоносова 10",
                    "Очень очень очень очень длинная причина невывоза"
                ) {}
                HorizontalDivider()
            }
            item {
                UnservedGarbageSiteListItem(
                    2,
                    "Ломоносова 31",
                    null
                ) {}
                HorizontalDivider()
            }
            item {
                UnservedGarbageSiteListItem(
                    3,
                    "Куцигина 64",
                    null
                ) {}
                HorizontalDivider()
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
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
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