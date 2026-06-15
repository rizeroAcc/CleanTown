package com.rizero.core_network.dto

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

//todo не факт что у путевого листа есть ID

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class ReportDTO(
    val reportID : String,
    val garbageSiteID : String,
    val waybillID : String,
    val reportDate : String,
    val collected : Boolean,
    val uncollectedReason : String?,
)
