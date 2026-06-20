package com.rizero.feature_finish_shift.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.UncollectedReason
import com.rizero.feature_finish_shift.store.FinishShiftStore.*

interface FinishShiftStore : Store<Intent, State, Label> {

    data class State(
        val uncollectedReason : UncollectedReason? = null,
        val uncollectedGarbageSites : UncollectedGarbageSites,
    ){
        sealed interface  UncollectedGarbageSites {
            data object Loading : UncollectedGarbageSites
            data class Loaded(val garbageSites : List<GarbageSite>) : UncollectedGarbageSites
        }
    }

    sealed interface Intent{
        data class ChangeUncollectedReason(
            val reason: UncollectedReason?
        ) : Intent
    }
    sealed interface Label{

    }
}

class FinishShiftStoreFactory(
    val storeFactory : StoreFactory = DefaultStoreFactory()
){
    sealed interface Action{

    }
    sealed interface Message{
        data class UncollectedReasonChanged(val reason : UncollectedReason?) : Message
    }
    fun create() : FinishShiftStore =
        object : FinishShiftStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FinishShiftStore",
            initialState = State(
                uncollectedReason = null,
                uncollectedGarbageSites = State.UncollectedGarbageSites.Loading
            ),
            bootstrapper = Bootstrapper(),
            executorFactory = { Executor() },
            reducer = DefaultReducer()
        ){

        }
    class Bootstrapper() : CoroutineBootstrapper<Action>(){
        override fun invoke() {

        }
    }
    class Executor() : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {
            when(intent){
                is Intent.ChangeUncollectedReason -> dispatch(Message.UncollectedReasonChanged(intent.reason))
            }
        }

        override fun executeAction(action: Action) {
            super.executeAction(action)
        }
    }
    class DefaultReducer : Reducer<State, Message>{
        override fun State.reduce(msg: Message): State {
            return when(msg){
                is Message.UncollectedReasonChanged -> copy(uncollectedReason = msg.reason)
            }
        }
    }
}