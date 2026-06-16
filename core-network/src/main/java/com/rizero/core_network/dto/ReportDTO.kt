package com.rizero.core_network.dto

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ReportDTO(
    val garbageSiteID : String,
    val collected : Boolean,
    val uncollectedReason : Int?,
)
