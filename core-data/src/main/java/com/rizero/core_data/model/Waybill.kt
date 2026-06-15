package com.rizero.core_data.model

import kotlin.uuid.Uuid

data class Waybill(
    val id : String,
    val number: Int,
    val date : String,
    val driver : String,
    val garbageSites: List<GarbageSite>
)
