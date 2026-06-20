package com.rizero.feature_take_photo.component

import android.net.Uri
import com.arkivanov.decompose.ComponentContext

interface SubmitPhotoComponent {
    val photoURI : Uri
    fun onAcceptPhoto()
    fun onDeclinePhoto()

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            photoUri: Uri,
            onAcceptPhotoCallback: (Uri) -> Unit,
            onDeclinePhotoCallback: () -> Unit,
        ) : SubmitPhotoComponent
    }
}

class MockSubmitPhotoComponent : SubmitPhotoComponent {
    override val photoURI: Uri
        get() = Uri.EMPTY
    override fun onAcceptPhoto() = Unit
    override fun onDeclinePhoto() = Unit
}