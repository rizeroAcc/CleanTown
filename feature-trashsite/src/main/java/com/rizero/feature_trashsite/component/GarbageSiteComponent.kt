package com.rizero.feature_trashsite.component

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.UncollectedReason
import com.rizero.feature_trashsite.component.DefaultGarbageSiteComponent.DialogConfig
import com.rizero.feature_trashsite.store.GarbageSiteStore
import com.rizero.feature_uncollect_reason.component.UncollectedReasonComponent
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface GarbageSiteComponent {
    val state : StateFlow<GarbageSiteStore.State>
    val uncollectedReasonDialog : Value<ChildSlot<DialogConfig, UncollectedReasonComponent>>
    fun changeCollectedStatus()
    fun openSelectUncollectedReasonDialog()
    fun closeSelectUncollectedReasonDialog()
    fun onBeforePhotoAccepted(photoUri : Uri)
    fun onAfterPhotoAccepted(photoUri : Uri)
    fun takeBeforePhoto()
    fun takeAfterPhoto()
    fun saveReport()
    fun navigateBack()
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigateBackCallback: (reportWritten : Boolean) -> Unit,
            takeBeforePhotoCallback : (address: String, onPhotoBeforeConfirmed: (Uri) -> Unit) -> Unit,
            takeAfterPhotoCallback : (address: String, onPhotoAfterConfirmed: (Uri) -> Unit) -> Unit,
            garbageSite: GarbageSite,
        ) : GarbageSiteComponent
    }
}

class MockGarbageSiteComponent(val mockState: GarbageSiteStore.State) : GarbageSiteComponent {
    override val state: StateFlow<GarbageSiteStore.State>
        get() = MutableStateFlow(mockState)
    override val uncollectedReasonDialog: Value<ChildSlot<DialogConfig, UncollectedReasonComponent>>
        get() = MutableValue(ChildSlot(null))

    override fun changeCollectedStatus() = Unit
    override fun openSelectUncollectedReasonDialog() = Unit
    override fun closeSelectUncollectedReasonDialog() = Unit
    override fun onBeforePhotoAccepted(photoUri: Uri) = Unit
    override fun onAfterPhotoAccepted(photoUri: Uri) = Unit
    override fun takeBeforePhoto() = Unit
    override fun takeAfterPhoto() = Unit
    override fun saveReport() = Unit
    override fun navigateBack() = Unit
}