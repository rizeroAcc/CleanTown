package com.rizero.feature_uncollect_reason.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.model.UncollectedReason
import com.rizero.core_data.repository.UncollectedReasonRepository
import com.rizero.feature_uncollect_reason.store.UncollectedReasonStore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface UncollectedReasonStore : Store<Intent, State, Label> {
    data class State(
        val ourUncollectedReasons : List<UncollectedReason> = emptyList(),
        val notOurUncollectedReasons : List<UncollectedReason> = emptyList()
    )
    sealed interface Intent{

    }
    sealed interface Label{

    }
}

class UncollectedReasonStoreFactory(
    val uncollectedReasonRepository: UncollectedReasonRepository,
    val storeFactory: StoreFactory = DefaultStoreFactory()
){

    sealed interface Message {
        data class CachedReasonsLoaded(val reasonList : List<UncollectedReason>) : Message
    }

    sealed interface Action {
        data object LoadCachedReasons : Action
    }

    fun create() : UncollectedReasonStore =
        object : UncollectedReasonStore, Store<Intent, State, Label> by storeFactory.create(
            name = "UncollectedReasonStore",
            autoInit = true,
            initialState = State(),
            bootstrapper = Bootstrapper(),
            executorFactory = { Executor(uncollectedReasonRepository) },
            reducer = DefaultReducer()
        ){

        }

    class Bootstrapper : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            dispatch(Action.LoadCachedReasons)
        }

    }

    class Executor(
        val uncollectedReasonRepository: UncollectedReasonRepository
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeAction(action: Action) {
            when(action){
                Action.LoadCachedReasons -> {
                    scope.launch(Dispatchers.Main) {
                        val cachedReasons = uncollectedReasonRepository.getAllUncollectedReasons()
                        dispatch(Message.CachedReasonsLoaded(cachedReasons))
                    }
                }
            }
        }
    }

    class DefaultReducer : Reducer<State, Message>{
        override fun State.reduce(
            msg: Message
        ): State {
            return when(msg){
                is Message.CachedReasonsLoaded -> {
                    copy(
                        ourUncollectedReasons = msg.reasonList.filter { it.our },
                        notOurUncollectedReasons = msg.reasonList.filter { !it.our }
                    )
                }
            }
        }

    }
}