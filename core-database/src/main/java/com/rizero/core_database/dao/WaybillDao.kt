package com.rizero.core_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.rizero.core_database.entity.WaybillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaybillDao {
    @Upsert
    suspend fun insertWaybill(waybill: WaybillEntity)
    @Delete
    suspend fun deleteWaybill(waybill: WaybillEntity)
    @Query("""
        SELECT * 
        FROM waybills 
        WHERE date =:date 
        AND driver =:driver
    """)
    fun getDailyWaybillUpdates(driver : String, date : String) : Flow<WaybillEntity?>

    @Query("""
        SELECT * 
        FROM waybills 
        WHERE date =:date 
        AND driver =:driver
    """)
    suspend fun getDailyWaybill(driver : String, date : String) : WaybillEntity?
}