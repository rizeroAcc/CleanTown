package com.rizero.core_database.di

import android.content.Context
import androidx.room.Room
import com.rizero.core_database.WaybillsDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
class DatabaseModule {
    @Single
    fun provideWaybillsDatabase(context: Context) : WaybillsDatabase =
        Room.databaseBuilder(
            context = context,
            klass = WaybillsDatabase::class.java,
            name = "WaybillsDatabase"
        )
            .fallbackToDestructiveMigration(true)
            .build()
}