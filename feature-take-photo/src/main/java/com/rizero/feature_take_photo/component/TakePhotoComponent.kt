package com.rizero.feature_take_photo.component

import android.net.Uri
import com.arkivanov.decompose.ComponentContext

interface TakePhotoComponent {
    fun navigateBack()
    fun onPhotoTaken(uri : Uri)

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onPhotoTakenCallback : (Uri) -> Unit,
            onNavigateBackCallback : () -> Unit,
        ) : TakePhotoComponent
    }
}

class MockTakePhotoComponent() : TakePhotoComponent {
    override fun navigateBack() = Unit
    override fun onPhotoTaken(uri : Uri) = Unit
}