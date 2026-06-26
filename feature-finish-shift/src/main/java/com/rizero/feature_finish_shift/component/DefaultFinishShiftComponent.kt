package com.rizero.feature_finish_shift.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.rizero.core_data.model.UncollectedReason
import com.rizero.core_data.repository.DriverRepository
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.feature_finish_shift.store.FinishShiftStore
import com.rizero.feature_finish_shift.store.FinishShiftStoreFactory
import com.rizero.feature_uncollect_reason.component.UncollectedReasonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

class DefaultFinishShiftComponent(
    componentContext: ComponentContext,
    val waybillRepository : WaybillRepository,
    val driverRepository: DriverRepository,
    val uncollectedReasonComponentFactory: UncollectedReasonComponent.Factory,
    val onUncollectedReasonWritten : ()-> Unit,
) : FinishShiftComponent, ComponentContext by componentContext{

    val componentScope = coroutineScope()
    val store = instanceKeeper.getStore {
        FinishShiftStoreFactory(waybillRepository,driverRepository).create()
    }
    init {
        componentScope.launch {
            store.labels.collect { label ->
                when(label) {
                    FinishShiftStore.Label.AllGarbageSitesCollected,
                    FinishShiftStore.Label.UncollectedReasonWritten -> {
                        onUncollectedReasonWritten()
                    }
                }
            }
        }
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

    override fun writeUncollectedReason() {
        store.accept(FinishShiftStore.Intent.WriteUncollectedReasons)
    }

    @Serializable
    data object DialogConfig
    @Single
    class Factory(
        val uncollectedReasonComponentFactory : UncollectedReasonComponent.Factory,
        val waybillRepository: WaybillRepository,
        val driverRepository: DriverRepository,
    ) : FinishShiftComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onUncollectedReasonWritten : ()-> Unit,
        ): FinishShiftComponent =
            DefaultFinishShiftComponent(
                componentContext = componentContext,
                waybillRepository = waybillRepository,
                driverRepository = driverRepository,
                uncollectedReasonComponentFactory = uncollectedReasonComponentFactory,
                onUncollectedReasonWritten = onUncollectedReasonWritten,
            )

    }
}