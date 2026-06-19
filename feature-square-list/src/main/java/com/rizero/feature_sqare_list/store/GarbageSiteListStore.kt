package com.rizero.feature_sqare_list.store

import android.location.Location
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.core_data.model.DriverCredentials
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Waybill
import com.rizero.core_data.repository.LocationRepository
import com.rizero.core_data.repository.UncollectedReasonRepository
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.feature_sqare_list.store.GarbageSiteListStore.*
import com.rizero.feature_sqare_list.store.GarbageSiteListStore.State.LocationState.*
import com.rizero.feature_sqare_list.store.GarbageSiteListStore.State.WaybillState.*
import com.rizero.feature_sqare_list.store.GarbageSiteListStoreFactory.Message.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.seconds

interface GarbageSiteListStore : Store<Intent, State, Label> {
    data class State(
        val waybillState : WaybillState,
        val currentLocationState : LocationState,
        val distanceToGarbageSitesCalculated : Boolean = false,
    ){
        sealed interface LocationState {
            data object LocationDisabled : LocationState
            data object Loading : LocationState
            data object LocationNotAllowed : LocationState
            data class LocationReceived(val location : Location) : LocationState
        }
        sealed interface WaybillState {
            data class Loaded(
                val waybill: Waybill,
                val loadTime : LocalDateTime,
                val synchronized : Boolean,
            ) : WaybillState
            data object Loading : WaybillState
            //todo Ошибка через sealed class
            data class LoadingError(val error : String) : WaybillState
            data object WaybillNotFound : WaybillState
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
    val waybillRepository: WaybillRepository,
    val uncollectedReasonRepository: UncollectedReasonRepository
){
    sealed interface Message {
        data class LocationStateUpdated(val locationState : State.LocationState) : Message
        data class WaybillLoaded(
            val waybill: Waybill,
            val loadTime : LocalDateTime,
            val synchronized : Boolean,
        ) : Message

        data class DistancesCalculated(val newGarbageSiteList : List<GarbageSite>) : Message
    }

    sealed interface Action {
        data object LoadDailyWaybill : Action
        data object UpdateUncollectedReasons : Action
        data object SubscribeLocationUpdates : Action

        data object RecalculateDistances : Action
    }

    fun create() : GarbageSiteListStore =
        object : GarbageSiteListStore, Store<Intent, State, Label>
        by storeFactory.create(
            name = "GarbageSiteListStore",
            autoInit = true,
            initialState = State(waybillState = State.WaybillState.Loading, currentLocationState = State.LocationState.Loading),
            bootstrapper = Bootstrapper(),
            executorFactory = { Executor(locationRepository, uncollectedReasonRepository, waybillRepository) },
            reducer = ReducerImpl()
        ) {}
    class Executor(
        val locationRepository: LocationRepository,
        val uncollectedReasonRepository: UncollectedReasonRepository,
        val waybillRepository: WaybillRepository,
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        var subscribedOnLocationUpdates : Boolean = false
        init {
            scope.launch {
                while (true){
                    if (!subscribedOnLocationUpdates){
                        forward(Action.SubscribeLocationUpdates)
                    }
                    delay(5000)
                }
            }
        }

        override fun executeIntent(intent: Intent) {

        }
        override fun executeAction(action: Action) {
            when(action) {
                Action.LoadDailyWaybill -> {
                    scope.launch(Dispatchers.IO) {
                        delay(1500)
                        withContext(Dispatchers.Main){
                            val loadTime = LocalDateTime.now()
                            val waybill = waybillRepository.fetchDailyWaybill("Driver", DriverCredentials("","")).fold(
                                ifLeft = { TODO("Когда будет реальный сервер") },
                                ifRight = { waybill ->
                                    dispatch(WaybillLoaded(waybill,loadTime,true))
                                }
                            )

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

                Action.SubscribeLocationUpdates -> {
                    if (!subscribedOnLocationUpdates) {
                        scope.launch(Dispatchers.IO) {
                            locationRepository.getLocationUpdates(10.seconds.inWholeMilliseconds)
                                .collect { location ->
                                    withContext(Dispatchers.Main) {
                                        dispatch(LocationStateUpdated(LocationReceived(location)))
                                        forward(action = Action.RecalculateDistances)
                                    }
                                }
                            subscribedOnLocationUpdates = false
                        }
                        subscribedOnLocationUpdates = true
                    }
                }

                Action.RecalculateDistances -> {
                    val state = state()
                    scope.launch(Dispatchers.Default) {
                        (state.currentLocationState as? LocationReceived)?.let { loadedLocation ->
                            val location = loadedLocation.location
                            val garbageSiteListWithDistance = (state.waybillState as Loaded).waybill.garbageSites.map {
                                it.copy(distanceTo = location.distanceToPoint(it.latitude,it.longitude))
                            }
                            withContext(Dispatchers.Main) {
                                dispatch(DistancesCalculated(garbageSiteListWithDistance))
                            }
                        }
                    }
                }
            }
        }
        fun calculateDistance(
            locationLat : Double,
            locationLon : Double,
            siteLat : Double,
            siteLon : Double
        ) : Int {
            val earthRadiusM = 6371008.8

            val f1 = Math.toRadians(locationLat)
            val f2 = Math.toRadians(siteLat)
            val deltaF = Math.toRadians(siteLat - locationLat)
            val deltaLambda = Math.toRadians(siteLon - locationLon)

            val a = sin(deltaF / 2) * sin(deltaF / 2) +
                    cos(f1) * cos(f2) * sin(deltaLambda / 2) * sin(deltaLambda / 2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return (earthRadiusM * c).toInt()
        }
        fun Location.distanceToPoint(siteLat : Double, siteLon : Double) : Int{
            return calculateDistance(
                locationLat = this.latitude,
                locationLon = this.longitude,
                siteLat = siteLat,
                siteLon = siteLon)
        }
    }
    class Bootstrapper : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            dispatch(Action.LoadDailyWaybill)
            dispatch(Action.UpdateUncollectedReasons)
            dispatch(Action.SubscribeLocationUpdates)
        }
    }
    class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State {
            return when(msg){
                is LocationStateUpdated -> copy(currentLocationState = msg.locationState)
                is WaybillLoaded -> copy(waybillState = Loaded(msg.waybill, msg.loadTime, msg.synchronized))
                is DistancesCalculated -> {
                    val loadedWaybill = this.waybillState as Loaded
                    copy(
                        waybillState = loadedWaybill.copy(
                            waybill = loadedWaybill.waybill.copy(
                                garbageSites = msg.newGarbageSiteList
                            )
                        ),
                        distanceToGarbageSitesCalculated = true,
                    )
                }
            }
        }
    }
}