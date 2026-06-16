package com.rizero.core_database.entity.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rizero.core_database.entity.ReportEntity
import com.rizero.core_database.entity.WaybillGarbageSite

data class WaybillGarbageSiteWithReport(
    @Embedded val garbageSite: WaybillGarbageSite,
    @Relation(
        entity = ReportEntity::class,
        parentColumn = "id",
        entityColumn = "waybillGarbageSiteID"
    )
    val report: ReportWithUncollectedReason?   // null, если отчёта нет
)