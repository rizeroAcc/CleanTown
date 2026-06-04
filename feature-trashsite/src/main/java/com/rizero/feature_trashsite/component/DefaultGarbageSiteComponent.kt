package com.rizero.feature_trashsite.component

import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Single

class DefaultGarbageSiteComponent(
    componentContext: ComponentContext,
    val navigateBackCallback : () -> Unit,
    val takePhotoCallback : () -> Unit,
) : GarbageSiteComponent, ComponentContext by componentContext {

    override fun takePhoto() {
        takePhotoCallback()
    }

    override fun navigateBack() {
        navigateBackCallback()
    }

    @Single
    class Factory : GarbageSiteComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            takePhotoCallback: () -> Unit,
            navigateBackCallback: () -> Unit,
        ): GarbageSiteComponent =
            DefaultGarbageSiteComponent(
                componentContext = componentContext,
                takePhotoCallback = takePhotoCallback,
                navigateBackCallback = navigateBackCallback
            )

    }
}