package com.rizero.core_database.di

import android.content.Context
import androidx.room.Room
import com.rizero.core_database.WaybillsDatabase
import com.rizero.core_database.dao.ReportDao
import com.rizero.core_database.dao.UncollectedReasonDao
import com.rizero.core_database.dao.WaybillDao
import com.rizero.core_database.dao.WaybillGarbageSiteDao
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
    @Single
    fun provideUncollectedReasonDao(database: WaybillsDatabase) : UncollectedReasonDao = database.uncollectedReasonDao()
    @Single
    fun provideWaybillDao(database: WaybillsDatabase) : WaybillDao = database.waybillDao()
    @Single
    fun provideWaybillGarbageSiteDao(database: WaybillsDatabase) : WaybillGarbageSiteDao = database.waybillGarbageSiteDao()
    @Single
    fun provideReportDao(database: WaybillsDatabase) : ReportDao = database.reportDao()
}