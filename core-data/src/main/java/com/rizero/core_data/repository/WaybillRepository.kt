package com.rizero.core_data.repository

import arrow.core.Either
import com.rizero.core_data.error.NetworkError
import com.rizero.core_data.model.DriverCredentials
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Report
import com.rizero.core_data.model.Waybill
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)
interface WaybillRepository {
    fun getLocalDailyWaybill(driverFullName : String) : Waybill?
    fun fetchDailyWaybill(driverFullName: String, driverCredentials: DriverCredentials) : Either<NetworkError, Waybill>
    fun checkWaybillUpdated(waybillID : Uuid, driverCredentials: DriverCredentials) : Either<NetworkError, Waybill>

    fun saveReport(report: Report)
    fun sendReport(waybillID : String, garbageSiteID : String, report : Report) : Either<NetworkError, Unit>
}