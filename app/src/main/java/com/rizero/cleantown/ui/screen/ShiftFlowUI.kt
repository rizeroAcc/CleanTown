package com.rizero.cleantown.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.rizero.cleantown.component.ShiftFlowComponent
import com.rizero.feature_sqare_list.ui.screen.SquareListScreen
import com.rizero.feature_take_photo.ui.screen.SubmitPhotoScreen
import com.rizero.feature_take_photo.ui.screen.TakePhotoScreen
import com.rizero.feature_trashsite.ui.screen.GarbageSiteScreen

@Composable
fun ShiftFlowUI(shiftFlowComponent: ShiftFlowComponent){
    Children(
        shiftFlowComponent.stack,
        modifier = Modifier.fillMaxSize()
    ) { child->
        when (val component = child.instance){
            is ShiftFlowComponent.Child.ConfirmPhotoPage -> SubmitPhotoScreen(component.submitPhotoComponent)
            is ShiftFlowComponent.Child.GarbageSiteList -> SquareListScreen(component.squareListComponent)
            is ShiftFlowComponent.Child.GarbageSitePage -> GarbageSiteScreen(component.garbageSiteComponent)
            is ShiftFlowComponent.Child.TakePhotoPage -> TakePhotoScreen(component.takePhotoComponent)
        }
    }
}