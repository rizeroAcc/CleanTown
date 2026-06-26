package com.rizero.feature_trashsite.component

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.UncollectedReason
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.feature_trashsite.store.GarbageSiteStore
import com.rizero.feature_trashsite.store.GarbageSiteStoreFactory
import com.rizero.feature_uncollect_reason.component.UncollectedReasonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

//TODO Список после сохранения отчета не обновляется, посмотреть почему
class DefaultGarbageSiteComponent(
    componentContext: ComponentContext,
    val waybillRepository: WaybillRepository,
    val navigateBackCallback : (reportWritten : Boolean) -> Unit,
    val takeBeforePhotoCallback : (address : String, onPhotoBeforeConfirmed: (Uri) -> Unit) -> Unit,
    val takeAfterPhotoCallback : (address : String, onPhotoAfterConfirmed: (Uri) -> Unit) -> Unit,
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
        GarbageSiteStoreFactory(waybillRepository).create(garbageSite)
    }
    val componentScope = coroutineScope()
    init {
        componentScope.launch(Dispatchers.Main){
            store.labels.collect { label ->
                when(label){
                    GarbageSiteStore.Label.ReportSaved -> navigateBackCallback(true)
                }
            }
        }

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
        takeBeforePhotoCallback(store.state.garbageSite.address){
            onBeforePhotoAccepted(it)
        }
    }

    override fun takeAfterPhoto() {
        takeAfterPhotoCallback(store.state.garbageSite.address){
            onAfterPhotoAccepted(it)
        }
    }

    override fun saveReport() {
        store.accept(GarbageSiteStore.Intent.SaveReport)
    }


    override fun navigateBack() {
        navigateBackCallback(false)
    }

    @Serializable
    data object DialogConfig

    @Single
    class Factory(
        val uncollectedReasonComponentFactory: UncollectedReasonComponent.Factory,
        val waybillRepository: WaybillRepository,
    ) : GarbageSiteComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            navigateBackCallback: (reportWritten : Boolean) -> Unit,
            takeBeforePhotoCallback : (address: String, onPhotoBeforeConfirmed: (Uri) -> Unit) -> Unit,
            takeAfterPhotoCallback : (address: String, onPhotoAfterConfirmed: (Uri) -> Unit) -> Unit,
            garbageSite: GarbageSite,
        ): GarbageSiteComponent =
            DefaultGarbageSiteComponent(
                componentContext = componentContext,
                waybillRepository = waybillRepository,
                takeBeforePhotoCallback = takeBeforePhotoCallback,
                takeAfterPhotoCallback = takeAfterPhotoCallback,
                navigateBackCallback = navigateBackCallback,
                uncollectedReasonComponentFactory = uncollectedReasonComponentFactory,
                garbageSite = garbageSite
            )

    }
}