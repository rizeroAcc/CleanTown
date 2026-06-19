package com.rizero.cleantown.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.rizero.cleantown.component.RootComponent.Child.*
import com.rizero.feature_request_permissions.component.RequestPermissionComponent
import com.rizero.featutre_signin.component.SignInComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

class RootComponent(
    componentContext: ComponentContext,
    val requestPermissionComponentFactory: RequestPermissionComponent.Factory,
    val shiftFlowComponentFactory: ShiftFlowComponent.Factory,
    val finishShiftFlowComponentFactory : FinishShiftFlowComponent.Factory,
    val signInComponentFactory: SignInComponent.Factory,
    val onPermissionRequest: (permissions: Array<String>, callback: () ->Unit ) -> Unit,
) : ComponentContext by componentContext{

    private val permissionNav = SlotNavigation<PermissionDialogConfig>()
    private val navigation = StackNavigation<Config>()

    val stack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.SignIn(),
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
            is Config.SignIn -> SignInC(
                signInComponentFactory(
                    componentContext = newComponentContext,
                    onAuthorizedCallback = {
                        navigation.replaceAll(Config.ShiftFlowComponent())
                    }
                )
            )
            is Config.ShiftFlowComponent -> ShiftFlowC(
                shiftFlowComponentFactory(
                    componentContext = newComponentContext,
                    finishShiftCallback = {
                        navigation.replaceAll(Config.ShiftFinishFlowComponent())
                    }
                )
            )

            is Config.ShiftFinishFlowComponent -> ShiftFinishFlowC(
                finishShiftFlowComponentFactory(
                    componentContext = newComponentContext
                )
            )
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
        class ShiftFlowC(val shiftFlowComponent: ShiftFlowComponent) : Child()
        class ShiftFinishFlowC(val finishShiftFlowComponent: FinishShiftFlowComponent) : Child()
    }

    @Serializable
    private data class PermissionDialogConfig(
        val tag: String,
    )
    @Serializable
    sealed interface Config {
        @Serializable
        class SignIn : Config
        @Serializable
        class ShiftFlowComponent : Config
        @Serializable
        class ShiftFinishFlowComponent : Config
    }
    @Single
    class Factory(
        val requestPermissionComponentFactory: RequestPermissionComponent.Factory,
        val shiftFlowComponentFactory: ShiftFlowComponent.Factory,
        val finishShiftFlowComponentFactory : FinishShiftFlowComponent.Factory,
        val signInComponentFactory: SignInComponent.Factory,
    ) {
        operator fun invoke(
            componentContext: ComponentContext,
            onPermissionRequest: (permissions: Array<String>, callback: () ->Unit ) -> Unit
        ) = RootComponent(
            componentContext = componentContext,
            requestPermissionComponentFactory = requestPermissionComponentFactory,
            shiftFlowComponentFactory = shiftFlowComponentFactory,
            finishShiftFlowComponentFactory = finishShiftFlowComponentFactory,
            signInComponentFactory = signInComponentFactory,
            onPermissionRequest = onPermissionRequest
        )
    }
}