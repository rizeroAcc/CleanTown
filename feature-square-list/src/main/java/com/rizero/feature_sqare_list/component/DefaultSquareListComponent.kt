package com.rizero.feature_sqare_list.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.repository.LocationRepository
import com.rizero.core_data.repository.UncollectedReasonRepository
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.feature_sqare_list.store.GarbageSiteListStoreFactory
import org.koin.core.annotation.Single

class DefaultSquareListComponent (
    componentContext: ComponentContext,
    val locationRepository: LocationRepository,
    val uncollectedReasonRepository: UncollectedReasonRepository,
    val waybillRepository: WaybillRepository,
    val garbageSiteSelectedCallback : (garbageSite : GarbageSite) -> Unit,
    val finishShiftCallback : () -> Unit,
    val storeFactory: StoreFactory = DefaultStoreFactory()
) : SquareListComponent, ComponentContext by componentContext {

    val store = instanceKeeper.getStore {
        GarbageSiteListStoreFactory(
            storeFactory = storeFactory,
            locationRepository = locationRepository,
            uncollectedReasonRepository = uncollectedReasonRepository,
            waybillRepository = waybillRepository
        ).create()
    }

    override val state = store.stateFlow(lifecycle)

    override fun openGarbageSite(garbageSite: GarbageSite) {
        garbageSiteSelectedCallback(garbageSite)
    }

    override fun finishShift() {
        finishShiftCallback()
    }

    @Single
    class Factory(
        val locationRepository: LocationRepository,
        val uncollectedReasonRepository: UncollectedReasonRepository,
        val waybillRepository: WaybillRepository,
    ) : SquareListComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            openGarbageSiteCallback : (garbageSite : GarbageSite) -> Unit,
            finishShiftCallback : () -> Unit,
        ): SquareListComponent =
            DefaultSquareListComponent(
                componentContext = componentContext,
                locationRepository = locationRepository,
                uncollectedReasonRepository = uncollectedReasonRepository,
                waybillRepository = waybillRepository,
                garbageSiteSelectedCallback = openGarbageSiteCallback,
                finishShiftCallback = finishShiftCallback
            )
    }
}