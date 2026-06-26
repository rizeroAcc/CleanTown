package com.rizero.core_data.model

import com.rizero.core_database.entity.WaybillGarbageSiteEntity
import com.rizero.core_database.entity.pojo.WaybillGarbageSiteWithReport
import com.rizero.core_network.dto.GarbageSiteDTO
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GarbageSite(
    val id : String,
    val address : String,
    val longitude : Double,
    val latitude : Double,
    val distanceTo : Int? = null,
    val report: Report? = null,
){
    companion object {
        fun fromDTO(garbageSiteDTO: GarbageSiteDTO) : GarbageSite =
            GarbageSite(
                id = garbageSiteDTO.id ?: UUID.randomUUID().toString(),
                address = garbageSiteDTO.address,
                latitude = garbageSiteDTO.latitude,
                longitude = garbageSiteDTO.longitude,
                distanceTo = null,
                report = null,
            )
        fun fromEntity(waybillGarbageSiteWithReport: WaybillGarbageSiteWithReport) : GarbageSite =
            GarbageSite(
                id = waybillGarbageSiteWithReport.garbageSite.id.toString(),
                address = waybillGarbageSiteWithReport.garbageSite.address,
                longitude = waybillGarbageSiteWithReport.garbageSite.longitude,
                latitude = waybillGarbageSiteWithReport.garbageSite.latitude,
                distanceTo = null,
                report = waybillGarbageSiteWithReport.report?.let { report->
                    Report.fromEntity(report)
                }
            )
    }
    fun toEntity(waybillID : UUID) : WaybillGarbageSiteEntity = WaybillGarbageSiteEntity(
        id = UUID.fromString(this.id),
        waybillID = waybillID,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude
    )

}
