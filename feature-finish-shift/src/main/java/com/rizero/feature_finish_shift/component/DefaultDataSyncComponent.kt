package com.rizero.feature_finish_shift.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.rizero.core_data.repository.DriverRepository
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.feature_finish_shift.store.SyncDataStore
import com.rizero.feature_finish_shift.store.SyncDataStoreFactory
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

class DefaultDataSyncComponent(
    componentContext: ComponentContext,
    val waybillRepository: WaybillRepository,
    val driverRepository: DriverRepository,
) : DataSyncComponent, ComponentContext by componentContext {

    val store = instanceKeeper.getStore {
        SyncDataStoreFactory(
            waybillRepository = waybillRepository,
            driverRepository = driverRepository,
        ).create()
    }
    override val state: StateFlow<SyncDataStore.State> = store.stateFlow(lifecycle)

    @Single
    class Factory(
        val waybillRepository: WaybillRepository,
        val driverRepository: DriverRepository,
    ) : DataSyncComponent.Factory{
        override fun invoke(componentContext: ComponentContext): DataSyncComponent =
            DefaultDataSyncComponent(
                componentContext,
                waybillRepository = waybillRepository,
                driverRepository = driverRepository,
            )
    }
}