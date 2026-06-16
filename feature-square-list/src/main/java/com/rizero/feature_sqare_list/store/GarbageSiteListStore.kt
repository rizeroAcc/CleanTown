package com.rizero.feature_sqare_list.store

import android.location.Location
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Waybill
import com.rizero.core_data.repository.LocationRepository
import com.rizero.core_data.repository.UncollectedReasonRepository
import com.rizero.feature_sqare_list.store.GarbageSiteListStore.*
import com.rizero.feature_sqare_list.store.GarbageSiteListStore.State.LocationState.*
import com.rizero.feature_sqare_list.store.GarbageSiteListStoreFactory.Message.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

interface GarbageSiteListStore : Store<Intent, State, Label> {
    data class State(
        val waybill : Waybill? = null,
        val currentLocation : LocationState,
    ){
        sealed interface LocationState {
            data object LocationDisabled : LocationState
            data object Loading : LocationState
            data object LocationNotReceived : LocationState
            data class LocationRecieved(val location : Location) : LocationState
        }
    }
    sealed interface Intent{
        data object UpdateLocation : Intent
    }
    sealed interface Label{

    }
}

class GarbageSiteListStoreFactory(
    val storeFactory: StoreFactory,
    val locationRepository: LocationRepository,
    val uncollectedReasonRepository: UncollectedReasonRepository
){
    sealed interface Message {
        data class LocationStateUpdated(val locationState : State.LocationState) : Message
        data class WaybillLoaded(val waybill: Waybill) : Message
    }

    sealed interface Action {
        data object LoadDailyWaybill : Action
        data object AccessCurrentLocation : Action

        data object UpdateUncollectedReasons : Action
    }

    fun create() : GarbageSiteListStore =
        object : GarbageSiteListStore, Store<Intent, State, Label>
        by storeFactory.create(
            name = "GarbageSiteListStore",
            autoInit = true,
            initialState = State(waybill = null, currentLocation = Loading),
            bootstrapper = Bootstrapper(),
            executorFactory = { Executor(locationRepository, uncollectedReasonRepository) },
            reducer = ReducerImpl()
        ) {}
    class Executor(
        val locationRepository: LocationRepository,
        val uncollectedReasonRepository: UncollectedReasonRepository
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {

        }
        override fun executeAction(action: Action) {
            when(action) {
                Action.AccessCurrentLocation -> {
                    dispatch(LocationStateUpdated(Loading))
                    if(!locationRepository.isLocationEnabled()){
                        dispatch(LocationStateUpdated(locationState = LocationDisabled))
                    }else{
                        scope.launch(Dispatchers.IO) {
                            val location = locationRepository.getCurrentLocation(timeoutMillis = 10.seconds.inWholeMilliseconds)
                            withContext(Dispatchers.Main){
                                if (location == null){
                                    dispatch(LocationStateUpdated(LocationNotReceived))
                                }else{
                                    dispatch(LocationStateUpdated(LocationRecieved(location)))
                                }
                            }
                        }
                    }
                }

                Action.LoadDailyWaybill -> {
                    scope.launch(Dispatchers.IO) {
                        delay(1500)
                        withContext(Dispatchers.Main){
                            dispatch(
                                WaybillLoaded(
                                    Waybill(
                                        date = "27.05.2026",
                                        driver = "Вася пупкин",
                                        garbageSites = listOf(
                                            GarbageSite(
                                                id = UUID.randomUUID().toString(),
                                                address = "Ломоносова 10",
                                                longitude = 51.252,
                                                latitude = 32.245,
                                                distanceTo = 100,
                                                report = null,
                                            ),
                                            GarbageSite(
                                                id = UUID.randomUUID().toString(),
                                                address = "9-е января 285",
                                                longitude = 51.2564,
                                                latitude = 32.432,
                                                distanceTo = 300,
                                                report = null,
                                            )
                                        ),
                                        id = UUID.randomUUID().toString()
                                    )
                                )
                            )
                            forward(Action.AccessCurrentLocation)
                        }
                    }
                }

                Action.UpdateUncollectedReasons -> {
                    scope.launch {
                        //todo обработка ошибок
                        val actualReasons = uncollectedReasonRepository.fetchUncollectedReasons()
                        uncollectedReasonRepository.updateCachedUncollectedReasonList(actualReasons)
                    }
                }
            }
        }
    }
    class Bootstrapper : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            dispatch(Action.LoadDailyWaybill)
            dispatch(Action.UpdateUncollectedReasons)
        }
    }
    class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State {
            return when(msg){
                is LocationStateUpdated -> copy(currentLocation = msg.locationState)
                is WaybillLoaded -> copy(waybill = msg.waybill)
            }
        }

    }
}