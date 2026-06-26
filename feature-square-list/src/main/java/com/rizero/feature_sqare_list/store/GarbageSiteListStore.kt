package com.rizero.feature_sqare_list.store

import android.location.Location
import android.util.Log
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.seconds

interface GarbageSiteListStore : Store<Intent, State, Label> {

    data class State(
        val waybillState: WaybillState = InitialLoading(DataSource.CACHE),
        val currentLocationState: LocationState = Loading,
    ) {
        sealed interface LocationState {
            data object Disabled : LocationState
            data object Loading : LocationState
            data object NotAllowed : LocationState
            data class LocationReceived(val location: Location) : LocationState
        }

        sealed interface WaybillState {
            data class InitialLoading(val dataSource: DataSource) : WaybillState
            data class Loaded(
                val waybill: Waybill,
                val nearestGarbageSites : List<GarbageSite>? = null,
                val isRefreshing: Boolean = false,
            ) : WaybillState
            data class Error(
                val message: String,
                val cachedWaybill: Waybill? = null,
                val nearestGarbageSites : List<GarbageSite>? = null,
                val isRefreshing: Boolean = false
            ) : WaybillState
        }

        enum class DataSource{
            CACHE,
            SERVER,
        }
    }

    sealed interface Intent {
        data object UpdateLocation : Intent
        data object RefreshWaybill : Intent
        data object UpdateWaybill : Intent
    }

    sealed interface Label
}

class GarbageSiteListStoreFactory(
    private val storeFactory: StoreFactory,
    private val locationRepository: LocationRepository,
    private val waybillRepository: WaybillRepository,
    private val uncollectedReasonRepository: UncollectedReasonRepository
) {
    sealed interface Message {
        data class LocationStateUpdated(val locationState: State.LocationState) : Message
        data class WaybillStateUpdated(val waybillState: State.WaybillState) : Message

        data object WaybillFetchStarted : Message
        data class DistancesCalculated(val garbageSites: List<GarbageSite>) : Message
    }

    sealed interface Action {
        data object SubscribeLocationUpdates : Action
        data object UpdateWaybillUpdatesSubscription : Action
        data object FetchDailyWaybill : Action
        data object UpdateUncollectedReasons : Action

        data object RecalculateDistances : Action

    }

    fun create(): GarbageSiteListStore =
        object : GarbageSiteListStore, Store<Intent, State, Label> by storeFactory.create(
            name = "GarbageSiteListStore",
            autoInit = true,
            initialState = State(),
            bootstrapper = Bootstrapper(),
            executorFactory = { Executor(locationRepository, waybillRepository, uncollectedReasonRepository) },
            reducer = ReducerImpl()
        ) {}

    private class Executor(
        private val locationRepository: LocationRepository,
        private val waybillRepository: WaybillRepository,
        private val uncollectedReasonRepository: UncollectedReasonRepository,
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        private var locationJob: Job? = null
        private var waybillJob: Job? = null

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.UpdateLocation -> forward(Action.SubscribeLocationUpdates)
                is Intent.RefreshWaybill -> forward(Action.FetchDailyWaybill)
                is Intent.UpdateWaybill -> forward(Action.UpdateWaybillUpdatesSubscription)
            }
        }

        override fun executeAction(action: Action) {
            when(action){
                Action.SubscribeLocationUpdates -> {
                    locationJob?.cancel()
                    locationJob = scope.launch(Dispatchers.IO) {
                        locationRepository.getLocationUpdates(10.seconds.inWholeMilliseconds)
                            .distinctUntilChanged()
                            .retryWhen{ cause, attempt->
                                Log.d("debug","retry subscribe to location updates")
                                delay(10000)
                                true
                            }
                            .collect { location ->
                                withContext(Dispatchers.Main) {
                                    dispatch(LocationStateUpdated(LocationReceived(location)))
                                    forward(Action.RecalculateDistances)
                                }
                            }
                    }
                }

                Action.UpdateWaybillUpdatesSubscription -> {
                    waybillJob?.cancel()
                    waybillJob = scope.launch(Dispatchers.IO) {
                        val date = getCurrentDateString()

                        // Сначала пытаемся взять из кэша
                        waybillRepository.getLocalDailyWaybillUpdates("Driver", date)
                            .distinctUntilChanged()
                            .collect { cached ->
                                withContext(Dispatchers.Main) {
                                    if (cached != null) {
                                        val unservedWaybill = cached.copy(
                                            garbageSites = cached.garbageSites.filter { it.report == null }
                                        )
                                        dispatch(
                                            WaybillStateUpdated(
                                                Loaded(
                                                    unservedWaybill
                                                )
                                            )
                                        )
                                        forward(Action.RecalculateDistances)
                                    } else {
                                        forward(Action.FetchDailyWaybill)
                                    }
                                }
                            }
                    }
                }

                Action.FetchDailyWaybill -> {
                    dispatch(WaybillFetchStarted)
                    val cachedWaybill = when(val waybillState = state().waybillState){
                        is Error -> waybillState.cachedWaybill
                        is Loaded -> waybillState.waybill
                        is InitialLoading -> null
                    }
                    scope.launch(Dispatchers.IO) {
                        waybillRepository.fetchDailyWaybill("Driver", DriverCredentials("", ""))
                            .onRight { waybill ->
                                waybillRepository.saveOrUpdateWaybill(waybill,cachedWaybill?.id?.let { UUID.fromString(it) })
                            }
                            .onLeft { error ->
                                // TODO: нормальная обработка ошибок
                                withContext(Dispatchers.Main) {
                                    dispatch(WaybillStateUpdated(Error("Ошибка загрузки", cachedWaybill)))
                                }
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

                Action.RecalculateDistances -> {
                    val state = state()
                    val location = (state.currentLocationState as? State.LocationState.LocationReceived)?.location ?: return
                    val loaded = state.waybillState as? State.WaybillState.Loaded ?: return

                    scope.launch(Dispatchers.Default) {
                        val updatedSites = loaded.waybill.garbageSites.map { site ->
                            site.copy(distanceTo = location.distanceToPoint(site.latitude, site.longitude))
                        }

                        withContext(Dispatchers.Main) {
                            dispatch(Message.DistancesCalculated(updatedSites))
                        }
                    }
                }
            }
        }

        private fun getCurrentDateString(): String {
            val date = LocalDate.now()
            return "${date.dayOfMonth}.${date.monthValue.toString().padStart(2, '0')}.${date.year}"
        }
        private fun Location.distanceToPoint(lat: Double, lon: Double): Int {
            val earthRadiusM = 6371008.8
            val f1 = Math.toRadians(latitude)
            val f2 = Math.toRadians(lat)
            val deltaF = Math.toRadians(lat - latitude)
            val deltaLambda = Math.toRadians(lon - longitude)

            val a = sin(deltaF / 2) * sin(deltaF / 2) +
                    cos(f1) * cos(f2) * sin(deltaLambda / 2) * sin(deltaLambda / 2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return (earthRadiusM * c).toInt()
        }
    }

    private class Bootstrapper : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.UpdateUncollectedReasons)
            dispatch(Action.UpdateWaybillUpdatesSubscription)
            dispatch(Action.SubscribeLocationUpdates)
        }
    }

    private class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is LocationStateUpdated -> copy(currentLocationState = msg.locationState)

            is WaybillStateUpdated -> copy(waybillState = msg.waybillState)

            is DistancesCalculated -> {
                when(waybillState){
                    is InitialLoading -> { return this }
                    is Error -> {
                        if (waybillState.cachedWaybill == null){
                            return this
                        }else{
                            copy(
                                waybillState = waybillState.copy(
                                    cachedWaybill = waybillState.cachedWaybill.copy(garbageSites = msg.garbageSites),
                                    nearestGarbageSites = msg.garbageSites
                                        .sortedBy { it.distanceTo }
                                        .take(3)
                                )
                            )
                        }
                    }

                    is Loaded -> {
                        copy(
                            waybillState = waybillState.copy(
                                waybill = waybillState.waybill.copy(garbageSites = msg.garbageSites),
                                nearestGarbageSites = msg.garbageSites
                                    .sortedBy { it.distanceTo }
                                    .take(3)
                            )
                        )
                    }
                }

            }

            WaybillFetchStarted -> copy(
                waybillState = when(waybillState){
                    is Error -> waybillState.copy(isRefreshing = true)
                    is InitialLoading -> waybillState.copy(dataSource = State.DataSource.SERVER)
                    is Loaded -> waybillState.copy(isRefreshing = true)
                }
            )
        }
    }


}