package com.rizero.cleantown.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.cleantown.component.RootComponent
import com.rizero.feature_request_permissions.ui.RequestPermissionScreen
import com.rizero.feature_signin.ui.screen.SignInScreen

@Composable
fun RootScreen(rootComponent: RootComponent){
    val permissionRequestDialog = rootComponent.permissionDialogSlot.subscribeAsState()
    Scaffold() { innerPadding->
        Children(
            stack = rootComponent.stack,
            modifier = Modifier.padding(innerPadding),
        ) { child->
            when(val component = child.instance){
                is RootComponent.Child.SignInC -> SignInScreen(component.signInComponent)
                is RootComponent.Child.ShiftFlowC -> ShiftFlowUI(component.shiftFlowComponent)
                is RootComponent.Child.ShiftFinishFlowC -> FinishShiftFlowUI(component.finishShiftFlowComponent)

            }
        }
        permissionRequestDialog.value.child?.let {
            Dialog(onDismissRequest = {

            }) {
                RequestPermissionScreen(it.instance)
            }
        }
    }
}