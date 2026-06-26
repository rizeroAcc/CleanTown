package com.rizero.feature_finish_shift.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Report
import com.rizero.core_data.model.UncollectedReason
import com.rizero.core_data.repository.DriverRepository
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.feature_finish_shift.store.FinishShiftStore.*
import com.rizero.feature_finish_shift.store.FinishShiftStore.State.UncollectedGarbageSites.*
import com.rizero.feature_finish_shift.store.FinishShiftStoreFactory.Message.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID

interface FinishShiftStore : Store<Intent, State, Label> {

    data class State(
        val uncollectedReason : UncollectedReason? = null,
        val uncollectedGarbageSites : UncollectedGarbageSites,
        val saving : Boolean = false,
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
        data object WriteUncollectedReasons : Intent
    }
    sealed interface Label{
        data object AllGarbageSitesCollected : Label
        data object UncollectedReasonWritten : Label
    }
}

class FinishShiftStoreFactory(
    val waybillRepository: WaybillRepository,
    val driverRepository: DriverRepository,
    val storeFactory : StoreFactory = DefaultStoreFactory()
){
    sealed interface Action{
        data object LoadUncollectedGarbageSites : Action
        data class WriteUncollectedReason(val reason: UncollectedReason) : Action
    }
    sealed interface Message{
        data class UncollectedReasonChanged(val reason : UncollectedReason?) : Message
        data class UncollectedGarbageSitesLoaded(val uncollectedGarbageSites : List<GarbageSite>) : Message
        data object SaveStarted : Message
    }
    fun create() : FinishShiftStore =
        object : FinishShiftStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FinishShiftStore",
            initialState = State(
                uncollectedReason = null,
                uncollectedGarbageSites = Loading
            ),
            bootstrapper = Bootstrapper(),
            executorFactory = { Executor(waybillRepository,driverRepository) },
            reducer = DefaultReducer()
        ){

        }
    class Bootstrapper() : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            dispatch(Action.LoadUncollectedGarbageSites)
        }
    }
    class Executor(
        val waybillRepository: WaybillRepository,
        val driverRepository: DriverRepository,
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {
            when(intent){
                is Intent.ChangeUncollectedReason -> dispatch(UncollectedReasonChanged(intent.reason))
                Intent.WriteUncollectedReasons -> {
                    val state = state()
                    if (state.uncollectedReason != null){
                        forward(Action.WriteUncollectedReason(state.uncollectedReason))
                    }
                }
            }
        }

        override fun executeAction(action: Action) {
            when(action){
                Action.LoadUncollectedGarbageSites -> {
                    val date = getCurrentDateString()
                    scope.launch(Dispatchers.IO) {
                        val driverCre = driverRepository.getSavedDriverCredentials()!!
                        //todo сделать ченибудь на null
                        val waybill = waybillRepository.getLocalDailyWaybill("Driver",date)!!
                        val uncollectedGarbageSites = waybill.garbageSites.filter { it.report == null }
                        if (uncollectedGarbageSites.isEmpty()){
                            withContext(Dispatchers.Main) { publish(Label.AllGarbageSitesCollected) }
                        }else{
                            withContext(Dispatchers.Main) {
                                dispatch(
                                    UncollectedGarbageSitesLoaded(
                                        uncollectedGarbageSites
                                    )
                                )
                            }
                        }
                    }

                }

                is Action.WriteUncollectedReason -> {
                    val state = state()
                    (state.uncollectedGarbageSites as? Loaded)?.garbageSites?.let { garbageSites ->
                        dispatch(SaveStarted)
                        scope.launch(Dispatchers.IO) {
                            garbageSites.forEach { site->
                                waybillRepository.saveReport(
                                    Report(
                                        id = UUID.randomUUID(),
                                        garbageSiteID = UUID.fromString(site.id),
                                        collected = false,
                                        photoBefore = null,
                                        photoAfter = null,
                                        uncollectedReason = action.reason
                                    )
                                )
                            }
                            withContext(Dispatchers.Main) {
                                publish(Label.UncollectedReasonWritten)
                            }
                        }
                    }
                }
            }
        }
        private fun getCurrentDateString(): String {
            val date = LocalDate.now()
            return "${date.dayOfMonth}.${date.monthValue.toString().padStart(2, '0')}.${date.year}"
        }
    }
    class DefaultReducer : Reducer<State, Message>{
        override fun State.reduce(msg: Message): State {
            return when(msg){
                is UncollectedReasonChanged -> copy(uncollectedReason = msg.reason)
                is UncollectedGarbageSitesLoaded -> copy(
                        uncollectedGarbageSites = Loaded(msg.uncollectedGarbageSites
                    )
                )

                SaveStarted -> copy(saving = true)
            }
        }
    }
}