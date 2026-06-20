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
import com.rizero.feature_trashsite.store.GarbageSiteStore.*
import com.rizero.feature_trashsite.store.GarbageSiteStoreFactory.Action.*
import java.util.UUID

interface GarbageSiteStore : Store<Intent, State, Label> {

    data class State(
        val garbageSite: GarbageSite,
        val report : Report
    )

    sealed interface Intent {
        data class ChangePhotoBefore(val uri : Uri) : Intent
        data class ChangePhotoAfter(val uri : Uri) : Intent
        data object ChangeGarbageCollectedStatus : Intent
        data class UncollectedReasonChanged(
            val uncollectedReason: UncollectedReason?
        ) : Intent
    }
    sealed interface Label {

    }
}

class GarbageSiteStoreFactory(
    val storeFactory: StoreFactory = DefaultStoreFactory()
) {
    sealed interface Action{

    }
    sealed interface Message{
        data object GarbageCollectedStatusChanged : Message
        data class UncollectedReasonChanged(val uncollectedReason: UncollectedReason?) : Message
        data class PhotoBeforeChanged(val uri : Uri) : Message
        data class PhotoAfterChanged(val uri : Uri) : Message
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
                Executor()
            },
            reducer = DefaultReducer()
        ){}
    class Executor : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {
            when(intent){
                Intent.ChangeGarbageCollectedStatus -> dispatch(Message.GarbageCollectedStatusChanged)
                is Intent.UncollectedReasonChanged -> dispatch(Message.UncollectedReasonChanged(intent.uncollectedReason))
                is Intent.ChangePhotoAfter -> dispatch(Message.PhotoAfterChanged(intent.uri))
                is Intent.ChangePhotoBefore -> dispatch(Message.PhotoBeforeChanged(intent.uri))
            }
        }

        override fun executeAction(action: Action) {

        }
    }
    class DefaultReducer : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State {
            return when(msg){
                Message.GarbageCollectedStatusChanged -> copy(report = report.copy(collected = !report.collected))
                is Message.UncollectedReasonChanged -> copy(report = report.copy(uncollectedReason = msg.uncollectedReason))
                is Message.PhotoAfterChanged -> copy(report = report.copy(photoAfter = msg.uri))
                is Message.PhotoBeforeChanged -> copy(report = report.copy(photoBefore = msg.uri))
            }
        }

    }

}