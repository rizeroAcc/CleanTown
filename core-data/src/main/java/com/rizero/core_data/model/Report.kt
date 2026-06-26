package com.rizero.core_data.model

import android.net.Uri
import com.rizero.core_database.entity.ReportEntity
import androidx.core.net.toUri
import com.rizero.core_data.serializer.UUIDSerializer
import com.rizero.core_data.serializer.UriSerializer
import com.rizero.core_database.entity.pojo.ReportWithUncollectedReason
import com.rizero.core_network.dto.ReportDTO
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class Report(
    @Serializable(with = UUIDSerializer::class)
    val id : UUID,
    @Serializable(with = UUIDSerializer::class)
    val garbageSiteID : UUID,
    val collected : Boolean,
    @Serializable(with = UriSerializer::class)
    val photoBefore : Uri?,
    @Serializable(with = UriSerializer::class)
    val photoAfter : Uri?,
    val uncollectedReason : UncollectedReason?,
    val send : Boolean = false,
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
        uncollectedReasonID = this.uncollectedReason?.id,
        send = send,
    )

fun Report.toDTO() : ReportDTO =
    ReportDTO(
        garbageSiteID = this.garbageSiteID.toString(),
        collected = this.collected,
        uncollectedReason = this.uncollectedReason?.id
    )
