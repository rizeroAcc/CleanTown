package com.rizero.core_network.dto

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data class GarbageSiteDTO(
    val id : String?,
    val address : String,
    val latitude : Double,
    val longitude : Double,
)
