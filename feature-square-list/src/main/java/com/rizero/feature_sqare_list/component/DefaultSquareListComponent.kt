package com.rizero.feature_sqare_list.component

import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Single

class DefaultSquareListComponent (
    componentContext: ComponentContext,
    val openGarbageSiteCallback : () -> Unit,
    val finishShiftCallback : () -> Unit,
) : SquareListComponent, ComponentContext by componentContext {
    override fun openGarbageSite() {
        openGarbageSiteCallback()
    }

    override fun finishShift() {
        finishShiftCallback()
    }

    @Single
    class Factory() : SquareListComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            openGarbageSiteCallback : () -> Unit,
            finishShiftCallback : () -> Unit,
        ): SquareListComponent =
            DefaultSquareListComponent(
                componentContext = componentContext,
                openGarbageSiteCallback = openGarbageSiteCallback,
                finishShiftCallback = finishShiftCallback
            )
    }
}