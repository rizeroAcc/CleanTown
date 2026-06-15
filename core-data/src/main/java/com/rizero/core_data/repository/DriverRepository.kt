package com.rizero.core_data.repository

import com.rizero.core_data.model.Driver
import com.rizero.core_data.model.DriverCredentials
import arrow.core.Either
import com.rizero.core_data.error.NetworkError

interface DriverRepository {
    suspend fun saveDriver(credentials: DriverCredentials) : Unit
    suspend fun getSavedDriver() : Driver?
    suspend fun getSavedDriverCredentials() : DriverCredentials?
    suspend fun deleteSavedDriver()

    suspend fun validateDriverCredentials(credentials: DriverCredentials) : Either<NetworkError, Boolean>
}