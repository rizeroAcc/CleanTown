package com.rizero.core_data.repository.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rizero.core_data.error.NetworkError
import com.rizero.core_data.model.Driver
import com.rizero.core_data.model.DriverCredentials
import com.rizero.core_data.repository.DriverRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import arrow.core.Either
import arrow.core.raise.either
import com.rizero.core_data.model.toDto
import com.rizero.core_network.AuthService
import com.rizero.core_network.dto.DriverCredentialsDTO

val Context.driverDataStore: DataStore<Preferences> by preferencesDataStore(name = "driver_prefs")

// Ключи
private val FULL_NAME_KEY = stringPreferencesKey("driver_full_name")
private val PASSWORD_KEY = stringPreferencesKey("driver_password")

@Single
class DefaultDriverRepository(
    context: Context,
    private val authService: AuthService,
) : DriverRepository {
    private val dataStore = context.driverDataStore

    override suspend fun saveDriver(credentials: DriverCredentials) {
        dataStore.edit { preferences ->
            preferences[FULL_NAME_KEY] = credentials.fullName
            preferences[PASSWORD_KEY] = credentials.password
        }
    }

    override suspend fun getSavedDriver(): Driver? {
        val fullName = dataStore.data
            .map { preferences -> preferences[FULL_NAME_KEY]}
            .firstOrNull()

        return fullName?.let { Driver(it) }
    }

    override suspend fun getSavedDriverCredentials(): DriverCredentials? {
        val preferences = dataStore.data.first()
        val fullName = preferences[FULL_NAME_KEY]
        val password = preferences[PASSWORD_KEY]
        return if (!fullName.isNullOrBlank() && !password.isNullOrBlank()) {
            DriverCredentials(fullName, password)
        } else {
            null
        }
    }

    override suspend fun deleteSavedDriver() {
        dataStore.edit { preferences ->
            preferences.remove(FULL_NAME_KEY)
            preferences.remove(PASSWORD_KEY)
        }
    }

    override suspend fun validateDriverCredentials(credentials: DriverCredentials) : Either<NetworkError, Boolean> = either {
        authService.authorizeDriver(driverCredentialsDTO = credentials.toDto())
    }
}