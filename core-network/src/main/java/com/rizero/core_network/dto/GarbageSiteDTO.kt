package com.rizero.core_network.dto

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class GarbageSiteDTO constructor(
    val guid : String,
    val address : String,
    val latitude : Double,
    val longitude : Double,
)
