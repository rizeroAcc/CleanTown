package com.rizero.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rizero.core_database.entity.UncollectedReasonEntity

@Dao
interface UncollectedReasonDao {

    @Insert
    suspend fun insert(uncollectedReasonEntity: UncollectedReasonEntity)

    //TODO Может стоит поменять на replace
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(uncollectedReasonEntity: List<UncollectedReasonEntity>)
    @Query(
        """
        SELECT * FROM uncollected_reasons WHERE active = 1
    """
    )
    suspend fun getAllActiveReasons() : List<UncollectedReasonEntity>
    @Query("""
        UPDATE uncollected_reasons SET active = 0 WHERE id = :reasonID
    """)
    suspend fun deactivateUncollectedReason(reasonID : Int)
}