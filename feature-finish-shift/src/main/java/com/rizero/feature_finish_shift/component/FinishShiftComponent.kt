package com.rizero.feature_finish_shift.component

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.rizero.feature_finish_shift.store.FinishShiftStore
import com.rizero.feature_uncollect_reason.component.MockUncollectedReasonComponent
import com.rizero.feature_uncollect_reason.component.UncollectedReasonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface FinishShiftComponent {
    val uncollectedReasonDialog : Value<ChildSlot<*,UncollectedReasonComponent>>
    val state : StateFlow<FinishShiftStore.State>
    fun selectUncollectedReason()

    fun interface Factory{
        operator fun invoke(
            componentContext: ComponentContext
        ) : FinishShiftComponent
    }
}

class MockFinishShiftComponent(
    val mockState : FinishShiftStore.State? = null,
    val dialogComponent : UncollectedReasonComponent? = null
) : FinishShiftComponent{

    override val uncollectedReasonDialog: Value<ChildSlot<*,UncollectedReasonComponent>>
        get() = MutableValue(
            initialValue = dialogComponent?.let {
                ChildSlot(Child.Created(Any(),dialogComponent))
            } ?:ChildSlot()
        )

    override val state: StateFlow<FinishShiftStore.State>
        get() = MutableStateFlow(mockState?: FinishShiftStore.State())

    override fun selectUncollectedReason() = Unit

}