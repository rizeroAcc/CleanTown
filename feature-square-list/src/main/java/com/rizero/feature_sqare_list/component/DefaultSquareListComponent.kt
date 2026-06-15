package com.rizero.feature_sqare_list.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.repository.LocationRepository
import com.rizero.core_data.repository.UncollectedReasonRepository
import com.rizero.feature_sqare_list.store.GarbageSiteListStore
import com.rizero.feature_sqare_list.store.GarbageSiteListStoreFactory
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

class DefaultSquareListComponent (
    componentContext: ComponentContext,
    val locationRepository: LocationRepository,
    val uncollectedReasonRepository: UncollectedReasonRepository,
    val openGarbageSiteCallback : () -> Unit,
    val finishShiftCallback : () -> Unit,
    val storeFactory: StoreFactory = DefaultStoreFactory()
) : SquareListComponent, ComponentContext by componentContext {

    val store = instanceKeeper.getStore {
        GarbageSiteListStoreFactory(
            storeFactory = storeFactory,
            locationRepository = locationRepository,
            uncollectedReasonRepository = uncollectedReasonRepository,
        ).create()
    }

    override val state = store.stateFlow(lifecycle)

    override fun openGarbageSite() {
        openGarbageSiteCallback()
    }

    override fun finishShift() {
        finishShiftCallback()
    }

    @Single
    class Factory(
        val locationRepository: LocationRepository,
        val uncollectedReasonRepository: UncollectedReasonRepository,
    ) : SquareListComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            openGarbageSiteCallback : () -> Unit,
            finishShiftCallback : () -> Unit,
        ): SquareListComponent =
            DefaultSquareListComponent(
                componentContext = componentContext,
                locationRepository = locationRepository,
                uncollectedReasonRepository = uncollectedReasonRepository,
                openGarbageSiteCallback = openGarbageSiteCallback,
                finishShiftCallback = finishShiftCallback
            )
    }
}