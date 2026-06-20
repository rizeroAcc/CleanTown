package com.rizero.feature_trashsite.component

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.UncollectedReason
import com.rizero.feature_trashsite.store.GarbageSiteStore
import com.rizero.feature_trashsite.store.GarbageSiteStoreFactory
import com.rizero.feature_uncollect_reason.component.UncollectedReasonComponent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

class DefaultGarbageSiteComponent(
    componentContext: ComponentContext,
    val navigateBackCallback : () -> Unit,
    val takeBeforePhotoCallback : (onPhotoBeforeConfirmed: (Uri) -> Unit) -> Unit,
    val takeAfterPhotoCallback : (onPhotoAfterConfirmed: (Uri) -> Unit) -> Unit,
    val uncollectedReasonComponentFactory: UncollectedReasonComponent.Factory,
    val garbageSite : GarbageSite,
) : GarbageSiteComponent, ComponentContext by componentContext {

    val uncollectedReasonDialogSlot = SlotNavigation<DialogConfig>()
    override val uncollectedReasonDialog : Value<ChildSlot<DialogConfig, UncollectedReasonComponent>> =
        childSlot(
            source = uncollectedReasonDialogSlot,
            serializer = DialogConfig.serializer()
        ){ configuration, context ->
            uncollectedReasonComponentFactory(
                componentContext = context,
                onReasonSelected = { reason ->
                    store.accept(GarbageSiteStore.Intent.UncollectedReasonChanged(reason))
                    if (store.state.report.collected) {
                        store.accept(GarbageSiteStore.Intent.ChangeGarbageCollectedStatus)
                    }
                    uncollectedReasonDialogSlot.dismiss()
                },
                onRemoveReasonCallback = {
                    store.accept(GarbageSiteStore.Intent.UncollectedReasonChanged(null))
                    uncollectedReasonDialogSlot.dismiss()
                }
            )
        }
    val store = instanceKeeper.getStore {
        GarbageSiteStoreFactory().create(garbageSite)
    }
    override val state: StateFlow<GarbageSiteStore.State> = store.stateFlow(lifecycle)
    override fun changeCollectedStatus() {
        store.accept(GarbageSiteStore.Intent.ChangeGarbageCollectedStatus)
    }

    override fun openSelectUncollectedReasonDialog() {
        uncollectedReasonDialogSlot.activate(DialogConfig)
    }

    override fun closeSelectUncollectedReasonDialog() {
        uncollectedReasonDialogSlot.dismiss()
    }

    override fun onBeforePhotoAccepted(photoUri: Uri) {
        store.accept(GarbageSiteStore.Intent.ChangePhotoBefore(photoUri))
    }

    override fun onAfterPhotoAccepted(photoUri: Uri) {
        store.accept(GarbageSiteStore.Intent.ChangePhotoAfter(photoUri))
    }

    override fun takeBeforePhoto() {
        takeBeforePhotoCallback{
            onBeforePhotoAccepted(it)
        }
    }

    override fun takeAfterPhoto() {
        takeAfterPhotoCallback{
            onAfterPhotoAccepted(it)
        }
    }


    override fun navigateBack() {
        navigateBackCallback()
    }

    @Serializable
    data object DialogConfig

    @Single
    class Factory(
        val uncollectedReasonComponentFactory: UncollectedReasonComponent.Factory,
    ) : GarbageSiteComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            navigateBackCallback: () -> Unit,
            takeBeforePhotoCallback : (onPhotoBeforeConfirmed: (Uri) -> Unit) -> Unit,
            takeAfterPhotoCallback : (onPhotoAfterConfirmed: (Uri) -> Unit) -> Unit,
            garbageSite: GarbageSite,
        ): GarbageSiteComponent =
            DefaultGarbageSiteComponent(
                componentContext = componentContext,
                takeBeforePhotoCallback = takeBeforePhotoCallback,
                takeAfterPhotoCallback = takeAfterPhotoCallback,
                navigateBackCallback = navigateBackCallback,
                uncollectedReasonComponentFactory = uncollectedReasonComponentFactory,
                garbageSite = garbageSite
            )

    }
}