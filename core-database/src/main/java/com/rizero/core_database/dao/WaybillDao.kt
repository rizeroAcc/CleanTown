package com.rizero.core_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rizero.core_database.entity.Waybill
import java.util.UUID

@Dao
interface WaybillDao {
    @Insert
    suspend fun insertWaybill(waybill: Waybill)
    @Delete
    suspend fun deleteWaybill(waybill: Waybill)
    @Query("""
        SELECT * 
        FROM waybills 
        WHERE date =:date 
        AND driver =:driver
    """)
    suspend fun getDailyWaybill(driver : String, date : String) : Waybill?
}