package com.rizero.core_data.repository.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import com.rizero.core_data.repository.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import java.security.Permissions
import kotlin.coroutines.resume

@Single
class DefaultLocationRepository(
    private val context: Context
) : LocationRepository {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun isLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            gpsEnabled || networkEnabled
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(timeoutMillis: Long): Location? {
        if (!isLocationEnabled()) return null

        return suspendCancellableCoroutine { continuation ->
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                    locationManager.removeUpdates(this)
                }
            }
            val timeoutJob = CoroutineScope(Dispatchers.Default).launch {  // scope из CoroutineScope, если есть
                delay(timeoutMillis)
                if (continuation.isActive) {
                    continuation.resume(null)  // таймаут — возвращаем null
                }
                locationManager.removeUpdates(locationListener)
            }
            try {
                val lastKnown = getLastKnownLocation()
                if (lastKnown != null) {
                    timeoutJob.cancel()
                    continuation.resume(lastKnown)
                    return@suspendCancellableCoroutine
                }

                // Запрашиваем обновление
                val providers = listOf(
                    LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER
                ).filter { locationManager.isProviderEnabled(it) }

                if (providers.isEmpty()) {
                    timeoutJob.cancel()
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }

                providers.forEach { provider ->
                    locationManager.requestSingleUpdate(
                        provider,
                        locationListener,
                        Looper.getMainLooper()
                    )
                }

                // Таймаут
                continuation.invokeOnCancellation {
                    timeoutJob.cancel()
                    locationManager.removeUpdates(locationListener)
                }

            } catch (e: SecurityException) {
                timeoutJob.cancel()
                continuation.resume(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        )

        return providers
            .filter { locationManager.isProviderEnabled(it) }
            .mapNotNull { locationManager.getLastKnownLocation(it) }
            .maxByOrNull { it.time }
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMillis : Long): Flow<Location> = callbackFlow {

        if (
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            close()
            return@callbackFlow
        }

        val listener = LocationListener {
            location -> trySend(location)
        }

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        )

        try {
            providers.forEach { provider ->
                locationManager.requestLocationUpdates(
                    provider,
                    intervalMillis,
                    30f,
                    listener,
                    Looper.getMainLooper()
                )
            }
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }
}