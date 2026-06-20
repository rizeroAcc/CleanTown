package com.rizero.feature_finish_shift.component

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.rizero.core_data.model.UncollectedReason
import com.rizero.feature_finish_shift.store.FinishShiftStore
import com.rizero.feature_finish_shift.store.FinishShiftStoreFactory
import com.rizero.feature_uncollect_reason.component.DefaultUncollectedReasonComponent
import com.rizero.feature_uncollect_reason.component.UncollectedReasonComponent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

class DefaultFinishShiftComponent(
    componentContext: ComponentContext,
    val uncollectedReasonComponentFactory: UncollectedReasonComponent.Factory
) : FinishShiftComponent, ComponentContext by componentContext{

    val store = instanceKeeper.getStore {
        FinishShiftStoreFactory().create()
    }
    val slotNav = SlotNavigation<DialogConfig>()

    override val uncollectedReasonDialog: Value<ChildSlot<DialogConfig,UncollectedReasonComponent>>
        = childSlot(
            source = slotNav,
            serializer = DialogConfig.serializer()
        ){ configuration, context ->
            uncollectedReasonComponentFactory(
                componentContext = context,
                onReasonSelected = { reason ->
                    store.accept(FinishShiftStore.Intent.ChangeUncollectedReason(reason))
                    slotNav.dismiss()
                },
                onRemoveReasonCallback = {
                    store.accept(FinishShiftStore.Intent.ChangeUncollectedReason(null))
                    slotNav.dismiss()
                }
            )
        }

    override val state: StateFlow<FinishShiftStore.State>
        get() = store.stateFlow(lifecycle)

    override fun selectUncollectedReason() {
        slotNav.activate(DialogConfig)
    }
    @Serializable
    data object DialogConfig
    @Single
    class Factory(
        val uncollectedReasonComponentFactory : UncollectedReasonComponent.Factory
    ) : FinishShiftComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext
        ): FinishShiftComponent =
            DefaultFinishShiftComponent(
                componentContext = componentContext,
                uncollectedReasonComponentFactory = uncollectedReasonComponentFactory
            )

    }
}