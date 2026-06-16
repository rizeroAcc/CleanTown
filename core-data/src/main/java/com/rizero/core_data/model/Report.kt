package com.rizero.core_data.model

import android.net.Uri
import com.rizero.core_database.entity.ReportEntity
import androidx.core.net.toUri
import com.rizero.core_database.entity.pojo.ReportWithUncollectedReason
import com.rizero.core_network.dto.ReportDTO
import java.util.UUID

data class Report(
    val id : Int,
    val garbageSiteID : UUID,
    val collected : Boolean,
    val photoBefore : Uri?,
    val photoAfter : Uri?,
    val uncollectedReason : UncollectedReason?
){
    companion object {
        fun fromEntity(reportEntity : ReportWithUncollectedReason) : Report =
            Report(
                id = reportEntity.report.reportID,
                garbageSiteID = reportEntity.report.waybillGarbageSiteID,
                collected = reportEntity.report.collected,
                photoBefore = reportEntity.report.photoBefore?.toUri(),
                photoAfter = reportEntity.report.photoAfter?.toUri(),
                uncollectedReason = reportEntity.uncollectedReason?.let { UncollectedReason.fromEntity(it) }
            )
    }
}

fun Report.toEntity() : ReportEntity =
    ReportEntity(
        reportID = this.id,
        waybillGarbageSiteID = this.garbageSiteID,
        photoBefore = this.photoBefore?.toString(),
        photoAfter = this.photoAfter?.toString(),
        collected = this.collected,
        uncollectedReasonID = this.uncollectedReason?.id
    )

fun Report.toDTO() : ReportDTO =
    ReportDTO(
        garbageSiteID = this.garbageSiteID.toString(),
        collected = this.collected,
        uncollectedReason = this.uncollectedReason?.id
    )
