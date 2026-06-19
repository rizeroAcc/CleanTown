package com.rizero.feature_finish_shift.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_finish_shift.store.SyncDataStore.*

interface SyncDataStore : Store<Intent, State, Label> {

    data class State(
        val foo : String = "",
    )

    sealed interface Intent{

    }
    sealed interface Label{

    }
}

class SyncDataStoreFactory(
    val storeFactory : StoreFactory = DefaultStoreFactory()
){
    sealed interface Action{

    }
    sealed interface Message{

    }
    fun create() : SyncDataStore =
        object : SyncDataStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SyncDataStore",
            initialState = State(),
            bootstrapper = Bootstrapper(),
            executorFactory = { Executor() },
            reducer = DefaultReducer()
        ){

        }
    class Bootstrapper() : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            TODO("Not yet implemented")
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