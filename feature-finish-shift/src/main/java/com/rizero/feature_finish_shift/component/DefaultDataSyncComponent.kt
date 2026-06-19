package com.rizero.feature_finish_shift.component

import com.rizero.feature_finish_shift.store.SyncDataStore
import kotlinx.coroutines.flow.StateFlow

class DefaultDataSyncComponent : DataSyncComponent {
    override val state: StateFlow<SyncDataStore.State>
        get() = TODO("Not yet implemented")

}