package com.rizero.feature_take_photo.component

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Single

class DefaultTakePhotoComponent(
    componentContext: ComponentContext,
    override val address : String,
    val onPhotoTakenCallback : (Uri) -> Unit,
    val onNavigateBackCallback : () -> Unit,
) : TakePhotoComponent, ComponentContext by componentContext {
    override fun navigateBack() {
        onNavigateBackCallback()
    }

    override fun onPhotoTaken(uri : Uri) {
        onPhotoTakenCallback(uri)
    }

    @Single
    class Factory : TakePhotoComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            address: String,
            onPhotoTakenCallback: (Uri) -> Unit,
            onNavigateBackCallback: () -> Unit
        ): TakePhotoComponent = DefaultTakePhotoComponent(
            componentContext = componentContext,
            address = address,
            onPhotoTakenCallback = onPhotoTakenCallback,
            onNavigateBackCallback = onNavigateBackCallback,
        )

    }
}