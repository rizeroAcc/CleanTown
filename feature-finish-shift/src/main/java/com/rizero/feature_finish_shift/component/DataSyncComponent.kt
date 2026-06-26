package com.rizero.feature_finish_shift.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.feature_finish_shift.store.SyncDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface DataSyncComponent {
    val state : StateFlow<SyncDataStore.State>
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ) : DataSyncComponent
    }
}

class MockDataSyncComponent(val mockState : SyncDataStore.State? = null) : DataSyncComponent {

    override val state: StateFlow<SyncDataStore.State>
        get() = MutableStateFlow(mockState?: SyncDataStore.State(
            syncState = SyncDataStore.State.SyncState.Init
        ))

}