package com.rizero.core_database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WaybillWithGarbageSites(
    @Embedded
    val waybill: Waybill,

    @Relation(
        parentColumn = "id",
        entityColumn = "waybillID",
        entity = WaybillGarbageSite::class,
        associateBy = Junction(
            value = WaybillGarbageSite::class,
            parentColumn = "waybillID",
            entityColumn = "garbageSiteGuid"
        )
    )
    val garbageSites: List<GarbageSite>
)