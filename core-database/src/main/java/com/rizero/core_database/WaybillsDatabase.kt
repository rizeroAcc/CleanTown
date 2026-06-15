package com.rizero.core_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rizero.core_database.entity.GarbageSite
import com.rizero.core_database.entity.Report
import com.rizero.core_database.entity.UncollectedReasonEntity
import com.rizero.core_database.entity.Waybill
import com.rizero.core_database.entity.WaybillGarbageSite

@Database(
    entities = [
        Waybill::class,
        GarbageSite::class,
        WaybillGarbageSite::class,
        Report::class,
        UncollectedReasonEntity::class
               ],
    version = 1
)
abstract class WaybillsDatabase : RoomDatabase() {

}