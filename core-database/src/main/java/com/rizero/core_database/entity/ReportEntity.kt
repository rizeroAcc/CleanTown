package com.rizero.core_database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "reports",
    foreignKeys = [
        ForeignKey(
            entity = WaybillGarbageSiteEntity::class,
            parentColumns = ["id"],
            childColumns = ["waybillGarbageSiteID"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),

    ]
)
data class ReportEntity(
    @PrimaryKey()
    val reportID : UUID,
    val waybillGarbageSiteID : UUID,
    val photoBefore : String?,
    val photoAfter : String?,
    val collected : Boolean,
    val uncollectedReasonID : Int?,
    val send : Boolean,
)