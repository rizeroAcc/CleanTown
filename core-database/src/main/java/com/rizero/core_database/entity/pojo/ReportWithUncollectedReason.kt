package com.rizero.core_database.entity.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.rizero.core_database.entity.ReportEntity
import com.rizero.core_database.entity.UncollectedReasonEntity

data class ReportWithUncollectedReason(
    @Embedded
    val report: ReportEntity,
    @Relation(
        parentColumn = "uncollectedReasonID",
        entityColumn = "id"
    )
    val uncollectedReason: UncollectedReasonEntity?
)