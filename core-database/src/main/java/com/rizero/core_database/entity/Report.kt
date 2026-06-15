package com.rizero.core_database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "reports",
    foreignKeys = [
        ForeignKey(
            entity = GarbageSite::class,
            parentColumns = ["guid"],
            childColumns = ["garbageSiteID"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Waybill::class,
            parentColumns = ["id"],
            childColumns = ["waybillID"],
            onUpdate = ForeignKey.CASCADE,
        )
    ]
)
data class Report(

    @PrimaryKey
    val reportID : UUID,

    val garbageSiteID : UUID,
    val waybillID : UUID,
    val photoBefore : String?,
    val photoAfter : String?,
    val collected : Boolean,
    val uncollectedReason : String?,

)