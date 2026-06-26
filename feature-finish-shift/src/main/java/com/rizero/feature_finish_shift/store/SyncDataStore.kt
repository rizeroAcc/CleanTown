package com.rizero.feature_finish_shift.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.model.Waybill
import com.rizero.core_data.repository.DriverRepository
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.feature_finish_shift.store.SyncDataStore.*
import com.rizero.feature_finish_shift.store.SyncDataStore.State.SyncState.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

interface SyncDataStore : Store<Intent, State, Label> {

    data class State(
        val syncState : SyncState
    ){
        sealed interface SyncState{
            data object Init : SyncState
            data class Sync(
                val total : Int,
                val syncCount : Int,
                val waybill: Waybill,
            ) : SyncState
            data object SyncFinished : SyncState
        }
    }

    sealed interface Intent{
        data object RestartSync : Intent
        data object Finish : Intent
    }
    sealed interface Label{

    }
}

class SyncDataStoreFactory(
    val waybillRepository: WaybillRepository,
    val driverRepository: DriverRepository,
    val storeFactory : StoreFactory = DefaultStoreFactory()
){
    sealed interface Action{
        data object StartSync : Action
        data object SyncData : Action
    }
    sealed interface Message{
        data class WaybillLoaded(
            val waybill: Waybill
        ) : Message
        data class SetAlreadySync(val synced : Int) : Message
        data object GarbageSiteSynced : Message
        data object SyncFinished : Message
    }
    fun create() : SyncDataStore =
        object : SyncDataStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SyncDataStore",
            initialState = State(syncState = State.SyncState.Init),
            bootstrapper = Bootstrapper(),
            executorFactory = { Executor(waybillRepository,driverRepository) },
            reducer = DefaultReducer()
        ){

        }
    class Bootstrapper() : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            dispatch(action = Action.StartSync)
        }
    }
    class Executor(
        val waybillRepository: WaybillRepository,
        val driverRepository: DriverRepository,
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {
            when(intent){
                Intent.Finish -> TODO()
                Intent.RestartSync -> forward(Action.SyncData)
            }
        }

        override fun executeAction(action: Action) {
            when(action) {
                Action.StartSync -> {
                    val date = getCurrentDateString()
                    scope.launch(Dispatchers.IO) {
                        val driverCre = driverRepository.getSavedDriverCredentials()!!
                        //todo сделать ченибудь на null
                        val waybill = waybillRepository.getLocalDailyWaybill("Driver", date)!!
                        withContext(Dispatchers.Main) {
                            dispatch(Message.WaybillLoaded(waybill))
                            forward(Action.SyncData)
                        }
                    }
                }
                Action.SyncData -> {
                    val waybill = (state().syncState as State.SyncState.Sync).waybill
                    val syncedCount = waybill.garbageSites.filter { it.report!!.send }.size
                    dispatch(Message.SetAlreadySync(syncedCount))
                    scope.launch(Dispatchers.IO) {


                        waybill.garbageSites.forEach { site->
                            if (!site.report!!.send) {
                                waybillRepository.sendReport(
                                    waybillID = waybill.id,
                                    garbageSiteID = site.id,
                                    report = site.report!!
                                ).onLeft {
                                    //todo прервать и в ошибку
                                    cancel()
                                }
                                waybillRepository.saveReport(site.report!!.copy(send = true))
                                withContext(Dispatchers.Main) {
                                    dispatch(Message.GarbageSiteSynced)
                                }
                            }
                        }
                        withContext(Dispatchers.Main) {
                            dispatch(Message.SyncFinished)
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
                is Message.WaybillLoaded -> copy(
                    syncState = Sync(
                        total = msg.waybill.garbageSites.size,
                        syncCount = 0,
                        waybill = msg.waybill
                    )
                )

                Message.GarbageSiteSynced -> copy(syncState = (this.syncState as Sync).copy(syncCount = this.syncState.syncCount + 1 ))
                Message.SyncFinished -> copy(syncState = SyncFinished)
                is Message.SetAlreadySync -> copy(syncState = (this.syncState as Sync).copy(syncCount = msg.synced))
            }
        }
    }
}