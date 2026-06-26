package com.rizero.feature_sqare_list.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.core_data.model.GarbageSite
import com.rizero.feature_sqare_list.store.GarbageSiteListStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SquareListComponent {

    val state : StateFlow<GarbageSiteListStore.State>
    fun openGarbageSite(garbageSite: GarbageSite)
    fun finishShift()
    fun fetchWaybill()
    fun updateWaybill()
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openGarbageSiteCallback : (garbageSite : GarbageSite, onGarbageSiteResult : (Boolean)-> Unit) -> Unit,
            finishShiftCallback : () -> Unit,
        ) : SquareListComponent
    }
}

class MockSquareListComponent(val mockState : StateFlow<GarbageSiteListStore.State>? = null) : SquareListComponent {
    override val state: StateFlow<GarbageSiteListStore.State>
        get() = mockState ?: MutableStateFlow(GarbageSiteListStore.State(
            GarbageSiteListStore.State.WaybillState.InitialLoading(GarbageSiteListStore.State.DataSource.CACHE),
            GarbageSiteListStore.State.LocationState.Loading
        ))

    override fun openGarbageSite(garbageSite: GarbageSite) = Unit
    override fun finishShift() = Unit
    override fun fetchWaybill() = Unit
    override fun updateWaybill() = Unit
}