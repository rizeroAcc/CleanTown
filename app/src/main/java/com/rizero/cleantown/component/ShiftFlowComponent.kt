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
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.rizero.core_data.model.GarbageSite
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ShiftFlowComponent(
    componentContext: ComponentContext,
    val squareListComponentFactory: SquareListComponent.Factory,
    val garbageSiteComponentFactory: GarbageSiteComponent.Factory,
    val takePhotoComponentFactory: TakePhotoComponent.Factory,
    val submitPhotoComponentFactory: SubmitPhotoComponent.Factory,
    val finishShiftCallback : () -> Unit
) : ComponentContext by componentContext{

    private val photoUriChannel = Channel<Uri?>(Channel.CONFLATED)
    private val garbageSiteResultChannel = Channel<Boolean?>(Channel.CONFLATED)
    private var subscriptionPhotoBefore : Job? = null
    private var subscriptionPhotoAfter : Job? = null
    private var subscriptionGarbageSiteResult : Job? = null
    private val navigation = StackNavigation<Config>()
    val componentScope = coroutineScope()
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
                    openGarbageSiteCallback = { garbageSite, resultWrittenCallback->
                        navigation.pushNew(Config.GarbageSitePage(garbageSite))
                        subscriptionGarbageSiteResult = componentScope.launch {
                            resultWrittenCallback(garbageSiteResultChannel.receive() ?: false)
                        }
                    },
                    finishShiftCallback = {
                        finishShiftCallback()
                    }
                )
            )
            is Config.GarbageSitePage -> Child.GarbageSitePage(
                garbageSiteComponentFactory(
                    componentContext = childComponentContext,
                    navigateBackCallback = { reportWritten->
                        componentScope.launch {
                            garbageSiteResultChannel.send( reportWritten)
                        }
                        navigation.pop()

                    },
                    garbageSite = config.garbageSite,
                    takeBeforePhotoCallback = { address,onPhotoBeforeConfirmedCallback->
                        navigation.pushNew(Config.TakePhotoPage(address))
                        subscriptionPhotoBefore = componentScope.launch {
                            photoUriChannel.receive()?.let {
                                onPhotoBeforeConfirmedCallback(it)
                            }
                        }
                    },
                    takeAfterPhotoCallback = { address,onPhotoAfterConfirmedCallback->
                        navigation.pushNew(Config.TakePhotoPage(address))
                        subscriptionPhotoAfter = componentScope.launch {
                            photoUriChannel.receive()?.let {
                                if(isActive) {
                                    onPhotoAfterConfirmedCallback(it)
                                }
                            }
                        }
                    }
                )
            )
            is Config.TakePhotoPage -> Child.TakePhotoPage(
                takePhotoComponentFactory.invoke(
                    componentContext = childComponentContext,
                    onPhotoTakenCallback = { uri->
                        navigation.pushNew(Config.ConfirmPhotoPage(uri.toString()))
                    },
                    onNavigateBackCallback = {
                        subscriptionPhotoAfter?.cancel()
                        subscriptionPhotoBefore?.cancel()
                        navigation.pop()
                    },
                    address = config.address
                )
            )
            is Config.ConfirmPhotoPage -> Child.ConfirmPhotoPage(
                submitPhotoComponentFactory.invoke(
                    componentContext = childComponentContext,
                    onAcceptPhotoCallback = { uri->
                        componentScope.launch {
                            photoUriChannel.send( uri)
                        }
                        navigation.popTo(1)
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
        data class GarbageSitePage(val garbageSite: GarbageSite) : Config
        @Serializable
        data class TakePhotoPage(
            val address : String,
        ) : Config
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