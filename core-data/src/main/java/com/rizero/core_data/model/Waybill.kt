package com.rizero.core_data.model

import com.rizero.core_database.entity.WaybillEntity
import com.rizero.core_network.dto.WaybillDTO
import java.time.LocalDateTime
import java.util.UUID

data class Waybill(
    val id : String,
    val date : String,
    val driver : String,
    val updateTime : LocalDateTime,
    val garbageSites: List<GarbageSite>
){
    companion object {
        fun fromDTO(waybillDTO: WaybillDTO) : Waybill =
            Waybill(
                id = waybillDTO.id,
                date = waybillDTO.date,
                driver = waybillDTO.driver,
                updateTime = LocalDateTime.now(),
                garbageSites = waybillDTO.garbageSiteList.map {
                    GarbageSite.fromDTO(it)
                }
            )
    }
    fun toEntity() : WaybillEntity = WaybillEntity(
        id = UUID.fromString(this.id),
        driver = this.driver,
        date = this.date,
        updateTime = this.updateTime.toString()
    )
}


