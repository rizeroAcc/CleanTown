package com.rizero.core_data.repository.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
import kotlin.coroutines.resume

class FusedLocationRepository(
    private val context: Context
) : LocationRepository {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun isLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(timeoutMillis: Long ): Location? {
        if (!hasLocationPermission() || !isLocationEnabled()) return null

        return suspendCancellableCoroutine { continuation ->
            val timeoutJob = CoroutineScope(Dispatchers.Default).launch {
                delay(timeoutMillis)
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }

            val cancellation = {
                timeoutJob.cancel()
            }

            try {
                // Сначала пробуем получить последний известный
                val lastLocationTask = fusedClient.lastLocation
                lastLocationTask.addOnSuccessListener { location ->
                    if (location != null && continuation.isActive) {
                        timeoutJob.cancel()
                        continuation.resume(location)
                    }
                }

                // Если lastLocation не подошёл — запрашиваем свежий
                lastLocationTask.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        return@addOnCompleteListener
                    }

                    val locationRequest = LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        timeoutMillis
                    )
                        .setWaitForAccurateLocation(true)
                        .setMaxUpdateAgeMillis(30000)
                        .build()

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            result.lastLocation?.let { location ->
                                if (continuation.isActive) {
                                    timeoutJob.cancel()
                                    continuation.resume(location)
                                }
                            }
                            fusedClient.removeLocationUpdates(this)
                        }
                    }

                    fusedClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )

                    continuation.invokeOnCancellation {
                        fusedClient.removeLocationUpdates(locationCallback)
                        timeoutJob.cancel()
                    }
                }
            } catch (e: Exception) {
                timeoutJob.cancel()
                continuation.resume(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMillis: Long): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(SecurityException("Location permission not granted"))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMillis
        )
            .setMinUpdateIntervalMillis(intervalMillis / 2)
            .setMinUpdateDistanceMeters(10f)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        try {
            fusedClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            fusedClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }
}