package com.rizero.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.rizero.core_database.entity.WaybillGarbageSite
import com.rizero.core_database.entity.pojo.WaybillGarbageSiteWithReport
import java.util.UUID

@Dao
interface WaybillGarbageSiteDao {
    @Insert
    suspend fun insertWaybillGarbageSite(waybillGarbageSite: WaybillGarbageSite)
    @Insert
    suspend fun insertWaybillGarbageSiteList(waybillGarbageSiteList: List<WaybillGarbageSite>)

    @Query("SELECT * FROM waybill_garbage_site WHERE waybillID = :waybillID")
    suspend fun getAllWithReports(waybillID: UUID): List<WaybillGarbageSiteWithReport>

    @Transaction
    @Query("SELECT * FROM waybill_garbage_site WHERE waybillID = :waybillID")
    suspend fun getAllWaybillGarbageSites(waybillID: UUID) : List<WaybillGarbageSite>
}