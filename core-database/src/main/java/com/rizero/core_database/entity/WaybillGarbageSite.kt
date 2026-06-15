package com.rizero.core_database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "waybill_garbage_sites",
    primaryKeys = ["waybillID", "garbageSiteGuid"],
    foreignKeys = [
        ForeignKey(
            entity = Waybill::class,
            parentColumns = ["id"],
            childColumns = ["waybillID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GarbageSite::class,
            parentColumns = ["guid"],
            childColumns = ["garbageSiteGuid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["waybillID","garbageSiteGuid"])
    ]
)
data class WaybillGarbageSite(
    val waybillID : UUID,
    val garbageSiteGuid: UUID
)