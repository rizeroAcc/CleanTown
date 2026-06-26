package com.rizero.cleantown.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.rizero.cleantown.component.FinishShiftFlowComponent
import com.rizero.cleantown.component.ShiftFlowComponent
import com.rizero.feature_finish_shift.ui.screen.DataSyncScreen
import com.rizero.feature_finish_shift.ui.screen.FinishShiftScreen
import com.rizero.feature_sqare_list.ui.screen.SquareListScreen
import com.rizero.feature_take_photo.ui.screen.SubmitPhotoScreen
import com.rizero.feature_take_photo.ui.screen.TakePhotoScreen
import com.rizero.feature_trashsite.ui.screen.GarbageSiteScreen

@Composable
fun FinishShiftFlowUI(finishShiftFlowComponent: FinishShiftFlowComponent){
    Children(
        finishShiftFlowComponent.stack,
        modifier = Modifier.fillMaxSize()
    ) { child->
        when (val component = child.instance){
            is FinishShiftFlowComponent.Child.FinishShiftC -> FinishShiftScreen(component.instance)
            is FinishShiftFlowComponent.Child.SyncDataC -> DataSyncScreen(component.instance)
        }
    }
}