package com.rizero.cleantown.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.rizero.cleantown.component.FinishShiftFlowComponent.Child.*
import com.rizero.feature_finish_shift.component.DataSyncComponent
import com.rizero.feature_finish_shift.component.DefaultFinishShiftComponent
import com.rizero.feature_finish_shift.component.FinishShiftComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

class FinishShiftFlowComponent(
    componentContext: ComponentContext,
    val finishShiftComponentFactory: FinishShiftComponent.Factory
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    val stack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.FinishShift,
        childFactory = ::createChild
    )

    fun createChild(
        config: Config,
        context: ComponentContext
    ) : Child {
        return when(config){
            Config.FinishShift -> FinishShiftC(
                instance = finishShiftComponentFactory.invoke(
                    componentContext = context
                )
            )
            Config.SyncData -> TODO()
        }
    }

    sealed class Child {
        data class FinishShiftC(val instance : FinishShiftComponent) : Child()
        data class SyncDataC(val instance : DataSyncComponent) : Child()
    }

    @Serializable
    sealed interface Config{
        @Serializable
        data object FinishShift : Config
        @Serializable
        data object SyncData : Config
    }


    @Single
    class Factory(
        val finishShiftComponentFactory: FinishShiftComponent.Factory
    ){
        operator fun invoke(
            componentContext: ComponentContext
        ) : FinishShiftFlowComponent = FinishShiftFlowComponent(
            componentContext = componentContext,
            finishShiftComponentFactory = finishShiftComponentFactory,
        )
    }
}