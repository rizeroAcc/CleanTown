package com.rizero.core_data.repository

import arrow.core.Either
import com.rizero.core_data.error.NetworkError
import com.rizero.core_data.model.DriverCredentials
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Report
import com.rizero.core_data.model.Waybill
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface WaybillRepository {
    suspend fun getLocalDailyWaybill(driver : String, date : String) : Waybill?
    suspend fun fetchDailyWaybill(driverFullName: String, driverCredentials: DriverCredentials) : Either<NetworkError, Waybill>
    suspend fun checkWaybillUpdated(waybillID : UUID, driverCredentials: DriverCredentials) : Either<NetworkError, Boolean>

    suspend fun saveReport(report: Report)
    suspend fun sendReport(waybillID : String, garbageSiteID : String, report : Report) : Either<NetworkError, Unit>
}