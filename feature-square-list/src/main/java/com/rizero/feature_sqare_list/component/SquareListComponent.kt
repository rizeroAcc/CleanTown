package com.rizero.feature_sqare_list.component

import com.arkivanov.decompose.ComponentContext

interface SquareListComponent {

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

class MockSquareListComponent : SquareListComponent {
    override fun openGarbageSite() = Unit
    override fun finishShift() = Unit
}