package com.rizero.core_data.model

import com.rizero.core_network.dto.WaybillDTO

data class Waybill(
    val id : String,
    val date : String,
    val driver : String,
    val garbageSites: List<GarbageSite>
){
    companion object {
        fun fromDTO(waybillDTO: WaybillDTO) : Waybill =
            Waybill(
                id = waybillDTO.id,
                date = waybillDTO.date,
                driver = waybillDTO.driver,
                garbageSites = waybillDTO.garbageSiteList.map {
                    GarbageSite.fromDTO(it)
                }
            )
    }
}


