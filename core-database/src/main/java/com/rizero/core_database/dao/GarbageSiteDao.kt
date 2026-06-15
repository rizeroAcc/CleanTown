package com.rizero.core_database.dao

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rizero.core_database.entity.GarbageSite
import com.rizero.core_database.entity.WaybillGarbageSite
import java.util.UUID

@Dao
interface GarbageSiteDao {

    @Query("""
        SELECT * FROM garbage_sites WHERE guid =:id
    """)
    suspend fun getGarbageSite(id : UUID) : GarbageSite?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGarbageSite(garbageSite: GarbageSite)

    @Query("""
        SELECT * FROM garbage_sites
        """)
    suspend fun getAllGarbageSites() : List<GarbageSite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaybillGarbageSite(waybillGarbageSite: WaybillGarbageSite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaybillGarbageSiteList(waybillGarbageSiteList: List<WaybillGarbageSite>)

    @Transaction
    suspend fun insertWaybillGarbageSites(waybillID : UUID, garbageSiteIDs : List<UUID>){
        insertWaybillGarbageSiteList(
            garbageSiteIDs.map {
                WaybillGarbageSite(waybillID = waybillID, garbageSiteGuid = it)
            }
        )
    }
}