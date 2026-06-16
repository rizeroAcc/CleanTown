package com.rizero.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rizero.core_database.entity.ReportEntity
import java.util.UUID

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("""
        SELECT *
        FROM reports
        WHERE waybillGarbageSiteID = :garbageSiteID
    """)
    suspend fun getGarbageSiteReport(garbageSiteID : UUID) : ReportEntity?

    @Transaction
    @Query("""
        SELECT r.* 
        FROM waybill_garbage_site as wgs 
        LEFT JOIN reports as r
        ON wgs.id = r.waybillGarbageSiteID
        WHERE wgs.waybillID =:waybillID
    """)
    suspend fun getAllWaybillReports(waybillID : UUID) : List<ReportEntity?>
}