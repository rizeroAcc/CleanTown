package com.rizero.cleantown.ui.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.router.slot.child
import com.rizero.cleantown.ui.component.RootComponent
import com.rizero.feature_request_permissions.ui.RequestPermissionScreen
import com.rizero.featutre_signin.ui.screen.SignInScreen

@Composable
fun RootScreen(rootComponent: RootComponent){
    Scaffold() { innerPadding->
        Children(
            stack = rootComponent.stack,
            modifier = Modifier.padding(innerPadding),
        ) { child->
            when(child.instance){
                is RootComponent.Child.SignInC -> SignInScreen()
            }
        }
        rootComponent.permissionDialogSlot.child?.let {
            Dialog(onDismissRequest = {

            }) {
                RequestPermissionScreen(it.instance)
            }
        }
    }
}