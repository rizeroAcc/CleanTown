package com.rizero.core_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rizero.core_database.dao.ReportDao
import com.rizero.core_database.dao.UncollectedReasonDao
import com.rizero.core_database.dao.WaybillDao
import com.rizero.core_database.dao.WaybillGarbageSiteDao
import com.rizero.core_database.entity.ReportEntity
import com.rizero.core_database.entity.UncollectedReasonEntity
import com.rizero.core_database.entity.Waybill
import com.rizero.core_database.entity.WaybillGarbageSite

@Database(
    entities = [
        Waybill::class,
        WaybillGarbageSite::class,
        ReportEntity::class,
        UncollectedReasonEntity::class
               ],
    version = 2
)
abstract class WaybillsDatabase : RoomDatabase() {
    abstract fun uncollectedReasonDao() : UncollectedReasonDao
    abstract fun reportDao() : ReportDao
    abstract fun waybillDao() : WaybillDao
    abstract fun waybillGarbageSiteDao() : WaybillGarbageSiteDao
}