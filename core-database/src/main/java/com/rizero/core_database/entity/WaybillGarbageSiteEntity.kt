package com.rizero.core_database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "waybill_garbage_site",
    foreignKeys = [
        ForeignKey(
            entity = WaybillEntity::class,
            parentColumns = ["id"],
            childColumns = ["waybillID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class WaybillGarbageSiteEntity(
    @PrimaryKey
    val id : UUID,
    val waybillID : UUID,
    val address : String,
    val latitude : Double,
    val longitude : Double,
)