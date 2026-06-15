package com.rizero.core_network

import com.rizero.core_network.dto.ReportDTO
import com.rizero.core_network.dto.WaybillDTO
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface WaybillService {
    suspend fun getDayliWaybill(driverFullName : String) : WaybillDTO
    suspend fun checkWaybillUpdated(driverFullName: String) : Boolean
    suspend fun sendReport(reportDTO: ReportDTO)
    @OptIn(ExperimentalUuidApi::class)
    suspend fun attachPhotoToReport(reportID : Uuid)
}