package com.rizero.cleantown.ui.component

import android.content.Context
import androidx.activity.result.ActivityResultCallback
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.rizero.cleantown.ui.component.RootComponent.Child.*
import com.rizero.feature_request_permissions.component.DefaultRequestPermissionComponent
import com.rizero.feature_request_permissions.component.RequestPermissionComponent
import com.rizero.featutre_signin.component.DefaultSignInComponent
import com.rizero.featutre_signin.component.SignInComponent
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
    val requestPermissionComponentFactory: RequestPermissionComponent.Factory,
    val onPermissionRequest: (permissions: Array<String>, callback: () ->Unit ) -> Unit,
) : ComponentContext by componentContext{

    private val permissionNav = SlotNavigation<PermissionDialogConfig>()
    private val navigation = StackNavigation<Config>()

    val stack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.SignIn,
        handleBackButton = true,
        childFactory = ::createChild
    )

    val permissionDialogSlot : Value<ChildSlot<*, RequestPermissionComponent>> = childSlot(
        source = permissionNav,
        serializer = PermissionDialogConfig.serializer(),
        handleBackButton = false
    ){ _, childComponentContext->
        requestPermissionComponentFactory(
            componentContext = childComponentContext,
            onPermissionRequest = { permissions,callback->
                onPermissionRequest(permissions,callback)
            },
            onAllPermissionsGranted = {
                closePermissionRequestDialog()
            },
        )
    }

    init {
        openPermissionRequestDialog()
    }
    fun createChild(
        config: Config,
        newComponentContext : ComponentContext,
    ) : Child{
        return when(config){
            Config.SignIn -> SignInC(DefaultSignInComponent(newComponentContext))
        }
    }

    fun openPermissionRequestDialog(){
        permissionNav.activate(PermissionDialogConfig("RequestPermissionDialog"))
    }

    fun closePermissionRequestDialog(){
        permissionNav.dismiss()
    }

    sealed class Child {
        class SignInC(val signInComponent: SignInComponent) : Child()
    }

    @Serializable
    private data class PermissionDialogConfig(
        val tag: String,
    )
    @Serializable
    sealed interface Config {
        data object SignIn : Config
    }
}