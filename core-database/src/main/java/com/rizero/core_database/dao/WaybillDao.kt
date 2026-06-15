package com.rizero.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rizero.core_database.entity.GarbageSite
import com.rizero.core_database.entity.Waybill
import com.rizero.core_database.entity.WaybillWithGarbageSites
import java.util.UUID

@Dao
interface WaybillDao {

    @Transaction
    @Query("SELECT * FROM waybills WHERE date =:date AND driver =:driver")
    suspend fun getWaybillWithGarbageSites(driver : String, date : String): WaybillWithGarbageSites?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWaybill(waybill: Waybill)


}