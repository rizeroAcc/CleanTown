package com.rizero.cleantown.component

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popToFirst
import com.arkivanov.decompose.router.stack.pushNew
import com.rizero.feature_sqare_list.component.SquareListComponent
import com.rizero.feature_take_photo.component.SubmitPhotoComponent
import com.rizero.feature_take_photo.component.TakePhotoComponent
import com.rizero.feature_trashsite.component.GarbageSiteComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import androidx.core.net.toUri

class ShiftFlowComponent(
    componentContext: ComponentContext,
    val squareListComponentFactory: SquareListComponent.Factory,
    val garbageSiteComponentFactory: GarbageSiteComponent.Factory,
    val takePhotoComponentFactory: TakePhotoComponent.Factory,
    val submitPhotoComponentFactory: SubmitPhotoComponent.Factory,
    val finishShiftCallback : () -> Unit
) : ComponentContext by componentContext{
    private val navigation = StackNavigation<Config>()

    val stack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.GarbageSiteList,
        childFactory = ::createChild,
        handleBackButton = true,
    )

    fun createChild(
        config: Config,
        childComponentContext : ComponentContext,
    ) : Child{
        return when(config){
            Config.GarbageSiteList -> Child.GarbageSiteList(
                squareListComponent = squareListComponentFactory(
                    childComponentContext,
                    openGarbageSiteCallback = {
                        navigation.pushNew(Config.GarbageSitePage)
                    },
                    finishShiftCallback = {
                        finishShiftCallback()
                    }
                )
            )
            Config.GarbageSitePage -> Child.GarbageSitePage(
                garbageSiteComponentFactory(
                    componentContext = childComponentContext,
                    takePhotoCallback = {
                        navigation.pushNew(Config.TakePhotoPage)
                    },
                    navigateBackCallback = {
                        navigation.pop()
                    }
                )
            )
            Config.TakePhotoPage -> Child.TakePhotoPage(
                takePhotoComponentFactory.invoke(
                    componentContext = childComponentContext,
                    onPhotoTakenCallback = { uri->

                        navigation.pushNew(Config.ConfirmPhotoPage(uri.toString()))
                    },
                    onNavigateBackCallback = {
                        navigation.pop()
                    },
                )
            )
            is Config.ConfirmPhotoPage -> Child.ConfirmPhotoPage(
                submitPhotoComponentFactory.invoke(
                    componentContext = childComponentContext,
                    onAcceptPhotoCallback = {
                        navigation.popToFirst()
                    },
                    onDeclinePhotoCallback = {
                        navigation.pop()
                    },
                    photoUri = config.photoUri.toUri()
                )
            )
        }
    }

    sealed class Child {
        class GarbageSiteList(val squareListComponent: SquareListComponent) : Child()
        class GarbageSitePage(val garbageSiteComponent: GarbageSiteComponent) : Child()
        class TakePhotoPage(val takePhotoComponent: TakePhotoComponent) : Child()
        class ConfirmPhotoPage(val submitPhotoComponent: SubmitPhotoComponent) : Child()
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object GarbageSiteList : Config
        @Serializable
        data object GarbageSitePage : Config
        @Serializable
        data object TakePhotoPage : Config
        @Serializable
        data class ConfirmPhotoPage (val photoUri : String) : Config
    }

    @Single
    class Factory(
        val squareListComponentFactory: SquareListComponent.Factory,
        val garbageSiteComponentFactory: GarbageSiteComponent.Factory,
        val takePhotoComponentFactory: TakePhotoComponent.Factory,
        val submitPhotoComponentFactory: SubmitPhotoComponent.Factory,
    ){
        operator fun invoke(
            componentContext: ComponentContext,
            finishShiftCallback : () -> Unit
        ) : ShiftFlowComponent =
            ShiftFlowComponent(
                componentContext = componentContext,
                squareListComponentFactory = squareListComponentFactory,
                garbageSiteComponentFactory = garbageSiteComponentFactory,
                takePhotoComponentFactory = takePhotoComponentFactory,
                submitPhotoComponentFactory = submitPhotoComponentFactory,
                finishShiftCallback = finishShiftCallback,
            )
    }
}