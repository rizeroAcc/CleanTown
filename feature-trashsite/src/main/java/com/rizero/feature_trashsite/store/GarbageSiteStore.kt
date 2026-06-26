package com.rizero.feature_trashsite.store

import android.net.Uri
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Report
import com.rizero.core_data.model.UncollectedReason
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.feature_trashsite.store.GarbageSiteStore.*
import com.rizero.feature_trashsite.store.GarbageSiteStoreFactory.Action.*
import com.rizero.feature_trashsite.store.GarbageSiteStoreFactory.Message.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

interface GarbageSiteStore : Store<Intent, State, Label> {

    data class State(
        val garbageSite: GarbageSite,
        val report : Report,
        val saving : Boolean = false,
    )

    sealed interface Intent {
        data class ChangePhotoBefore(val uri : Uri) : Intent
        data class ChangePhotoAfter(val uri : Uri) : Intent
        data object ChangeGarbageCollectedStatus : Intent
        data class UncollectedReasonChanged(
            val uncollectedReason: UncollectedReason?
        ) : Intent

        data object SaveReport : Intent
    }
    sealed interface Label {
        data object ReportSaved : Label
    }
}

class GarbageSiteStoreFactory(
    val waybillRepository: WaybillRepository,
    val storeFactory: StoreFactory = DefaultStoreFactory()
) {
    sealed interface Action{
        data object SaveReport : Action
    }
    sealed interface Message{
        data object GarbageCollectedStatusChanged : Message
        data class UncollectedReasonChanged(val uncollectedReason: UncollectedReason?) : Message
        data class PhotoBeforeChanged(val uri : Uri) : Message
        data class PhotoAfterChanged(val uri : Uri) : Message

        data object SaveStarted : Message
    }
    fun create(garbageSite: GarbageSite) : GarbageSiteStore =
        object : GarbageSiteStore, Store<Intent, State, Label> by storeFactory.create(
            name = "GarbageSiteStore",
            autoInit = true,
            initialState = State(
                garbageSite = garbageSite,
                report = Report(
                    id = UUID.randomUUID(),
                    garbageSiteID = UUID.fromString(garbageSite.id),
                    collected = false,
                    photoBefore = null,
                    photoAfter = null,
                    uncollectedReason = null,
                )
            ),
            bootstrapper = null,
            executorFactory = {
                Executor(waybillRepository)
            },
            reducer = DefaultReducer()
        ){}
    class Executor(
        val waybillRepository: WaybillRepository,
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {
            when(intent){
                Intent.ChangeGarbageCollectedStatus -> dispatch(GarbageCollectedStatusChanged)
                is Intent.UncollectedReasonChanged -> dispatch(UncollectedReasonChanged(intent.uncollectedReason))
                is Intent.ChangePhotoAfter -> dispatch(PhotoAfterChanged(intent.uri))
                is Intent.ChangePhotoBefore -> dispatch(PhotoBeforeChanged(intent.uri))
                Intent.SaveReport -> forward(SaveReport)
            }
        }

        override fun executeAction(action: Action) {
            when(action){
                SaveReport -> {
                    val state = state()
                    dispatch(Message.SaveStarted)
                    scope.launch(Dispatchers.IO) {
                        waybillRepository.saveReport(report = state.report)
                        withContext(Dispatchers.Main) {
                            publish(Label.ReportSaved)
                        }
                    }
                }
            }
        }
    }
    class DefaultReducer : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State {
            return when(msg){
                GarbageCollectedStatusChanged -> copy(report = report.copy(collected = !report.collected))
                is UncollectedReasonChanged -> copy(report = report.copy(uncollectedReason = msg.uncollectedReason))
                is PhotoAfterChanged -> copy(report = report.copy(photoAfter = msg.uri))
                is PhotoBeforeChanged -> copy(report = report.copy(photoBefore = msg.uri))
                SaveStarted -> copy(saving = true)
            }
        }

    }

}