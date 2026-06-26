package com.rizero.core_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.rizero.core_database.entity.WaybillGarbageSiteEntity
import com.rizero.core_database.entity.pojo.WaybillGarbageSiteWithReport
import java.util.UUID

@Dao
interface WaybillGarbageSiteDao {
    @Upsert
    suspend fun insertWaybillGarbageSite(waybillGarbageSite: WaybillGarbageSiteEntity)
    @Upsert
    suspend fun insertWaybillGarbageSiteList(waybillGarbageSiteList: List<WaybillGarbageSiteEntity>)

    @Delete
    suspend fun deleteWaybillGarbageSiteList(garbageSites : List<WaybillGarbageSiteEntity>)

    @Query("SELECT * FROM waybill_garbage_site WHERE waybillID = :waybillID")
    suspend fun getAllWithReports(waybillID: UUID): List<WaybillGarbageSiteWithReport>

    @Transaction
    @Query("SELECT * FROM waybill_garbage_site WHERE waybillID = :waybillID")
    suspend fun getAllWaybillGarbageSites(waybillID: UUID) : List<WaybillGarbageSiteEntity>
}