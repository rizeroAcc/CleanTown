package com.rizero.feature_trashsite.component

import com.arkivanov.decompose.ComponentContext

interface GarbageSiteComponent {
    fun takePhoto()
    fun navigateBack()
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            takePhotoCallback: () -> Unit,
            navigateBackCallback: () -> Unit,
        ) : GarbageSiteComponent
    }
}

class MockGarbageSiteComponent() : GarbageSiteComponent {
    override fun takePhoto() = Unit
    override fun navigateBack() = Unit
}