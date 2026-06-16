package com.rizero.feature_uncollect_reason.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.core_data.model.UncollectedReason
import com.rizero.feature_uncollect_reason.store.UncollectedReasonStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface UncollectedReasonComponent {
    val state : StateFlow<UncollectedReasonStore.State>

    fun onReasonSelected(selectedReason : UncollectedReason) : Unit
    fun interface Factory{
        operator fun invoke(
            componentContext: ComponentContext,
            onReasonSelected : (UncollectedReason)-> Unit
        ) : UncollectedReasonComponent
    }
}

class MockUncollectedReasonComponent(val mockState : UncollectedReasonStore.State? = null) : UncollectedReasonComponent{
    override val state: StateFlow<UncollectedReasonStore.State>
        get() = MutableStateFlow(mockState ?: UncollectedReasonStore.State())

    override fun onReasonSelected(selectedReason: UncollectedReason) = Unit

}