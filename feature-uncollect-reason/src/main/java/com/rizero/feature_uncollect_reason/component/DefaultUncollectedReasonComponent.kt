package com.rizero.feature_uncollect_reason.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.rizero.core_data.model.UncollectedReason
import com.rizero.core_data.repository.UncollectedReasonRepository
import com.rizero.feature_uncollect_reason.store.UncollectedReasonStoreFactory
import org.koin.core.annotation.Single

class DefaultUncollectedReasonComponent(
    componentContext: ComponentContext,
    val uncollectedReasonRepository: UncollectedReasonRepository,
    val onReasonSelectedCallback : (UncollectedReason) -> Unit,
    val onRemoveReasonCallback : () -> Unit,
) : UncollectedReasonComponent, ComponentContext by componentContext{
    val store = instanceKeeper.getStore {
        UncollectedReasonStoreFactory(
            uncollectedReasonRepository = uncollectedReasonRepository,
        ).create()
    }

    override val state = store.stateFlow(lifecycle)
    override fun onReasonSelected(selectedReason: UncollectedReason) {
        onReasonSelectedCallback(selectedReason)
    }

    override fun removeUncollectedReason() {
        onRemoveReasonCallback()
    }

    @Single
    class Factory(
        val uncollectedReasonRepository: UncollectedReasonRepository
    ) : UncollectedReasonComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onReasonSelected: (UncollectedReason) -> Unit,
            onRemoveReasonCallback : () -> Unit,
        ) = DefaultUncollectedReasonComponent(
                componentContext = componentContext,
                uncollectedReasonRepository = uncollectedReasonRepository,
                onRemoveReasonCallback = onRemoveReasonCallback,
                onReasonSelectedCallback = onReasonSelected,
            )
    }
}

