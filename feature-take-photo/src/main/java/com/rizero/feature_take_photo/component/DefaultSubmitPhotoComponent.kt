package com.rizero.feature_take_photo.component

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Single

class DefaultSubmitPhotoComponent(
    componentContext: ComponentContext,
    override val photoURI : Uri,
    val onAcceptPhotoCallback: (Uri) -> Unit,
    val onDeclinePhotoCallback: () -> Unit,
) : SubmitPhotoComponent, ComponentContext by componentContext{
    override fun onAcceptPhoto() {
        onAcceptPhotoCallback(photoURI)
    }

    override fun onDeclinePhoto() {
        onDeclinePhotoCallback()
    }

    @Single
    class Factory : SubmitPhotoComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            photoUri : Uri,
            onAcceptPhotoCallback: (Uri) -> Unit,
            onDeclinePhotoCallback: () -> Unit
        ): SubmitPhotoComponent =
            DefaultSubmitPhotoComponent(
                componentContext = componentContext,
                onAcceptPhotoCallback = onAcceptPhotoCallback,
                onDeclinePhotoCallback = onDeclinePhotoCallback,
                photoURI = photoUri
            )
    }
}