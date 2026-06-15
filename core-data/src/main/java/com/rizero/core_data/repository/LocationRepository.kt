package com.rizero.core_data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurrentLocation(timeoutMillis: Long = 10000): Location?
    fun getLocationUpdates(intervalMillis : Long = 60000): Flow<Location>
    fun isLocationEnabled(): Boolean
}