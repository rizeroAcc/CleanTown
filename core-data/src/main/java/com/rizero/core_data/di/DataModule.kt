package com.rizero.core_data.di

import android.content.Context
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.rizero.core_data.repository.LocationRepository
import com.rizero.core_data.repository.impl.DefaultLocationRepository
import com.rizero.core_data.repository.impl.FusedLocationRepository
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.rizero.core_data")
@Configuration
class DataModule {
    @Single
    fun provideLocationRepository(context : Context) : LocationRepository =
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            Log.d("KOIN","Fused provided")
            FusedLocationRepository(context)
        } else {
            DefaultLocationRepository(context)
        }

}