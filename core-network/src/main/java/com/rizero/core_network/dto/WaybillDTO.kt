package com.rizero.core_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class WaybillDTO(
    val driver : String,
    val date : String,
    val number : Int,
    val garbageSiteList : List<GarbageSiteDTO>
)
