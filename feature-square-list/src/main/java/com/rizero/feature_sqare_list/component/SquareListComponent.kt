package com.rizero.feature_sqare_list.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.feature_sqare_list.store.GarbageSiteListStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SquareListComponent {

    val state : StateFlow<GarbageSiteListStore.State>
    //Todo через интенты
    fun openGarbageSite()
    fun finishShift()
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openGarbageSiteCallback : () -> Unit,
            finishShiftCallback : () -> Unit,
        ) : SquareListComponent
    }
}

class MockSquareListComponent(val mockState : StateFlow<GarbageSiteListStore.State>? = null) : SquareListComponent {
    override val state: StateFlow<GarbageSiteListStore.State>
        get() = mockState ?: MutableStateFlow(GarbageSiteListStore.State(
            null,
            GarbageSiteListStore.State.LocationState.Loading
        ))

    override fun openGarbageSite() = Unit
    override fun finishShift() = Unit
}