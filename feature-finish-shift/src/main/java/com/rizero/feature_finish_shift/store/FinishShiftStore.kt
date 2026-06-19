package com.rizero.feature_finish_shift.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.model.UncollectedReason
import com.rizero.feature_finish_shift.store.FinishShiftStore.*

interface FinishShiftStore : Store<Intent, State, Label> {

    data class State(
        val uncollectedReason : UncollectedReason? = null
    )

    sealed interface Intent{
        data class ChangeUncollectedReason(
            val reason: UncollectedReason
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

    }
    fun create() : FinishShiftStore =
        object : FinishShiftStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FinishShiftStore",
            initialState = State(),
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
            super.executeIntent(intent)
        }

        override fun executeAction(action: Action) {
            super.executeAction(action)
        }
    }
    class DefaultReducer : Reducer<State, Message>{
        override fun State.reduce(msg: Message): State {
            TODO("Not yet implemented")
        }
    }
}