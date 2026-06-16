package com.rizero.core_data.repository.impl

import arrow.core.Either
import arrow.core.raise.either
import com.rizero.core_data.error.NetworkError
import com.rizero.core_data.model.DriverCredentials
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Report
import com.rizero.core_data.model.Waybill
import com.rizero.core_data.model.toDTO
import com.rizero.core_data.model.toEntity
import com.rizero.core_data.repository.WaybillRepository
import com.rizero.core_database.dao.ReportDao
import com.rizero.core_database.dao.WaybillDao
import com.rizero.core_database.dao.WaybillGarbageSiteDao
import com.rizero.core_network.WaybillService
import org.koin.core.annotation.Single
import java.util.UUID
import kotlin.uuid.Uuid

@Single
class DefaultWaybillRepository(
    val waybillService: WaybillService,
    val waybillDao: WaybillDao,
    val waybillGarbageSiteDao: WaybillGarbageSiteDao,
    val reportDao: ReportDao
) : WaybillRepository {

    override suspend fun getLocalDailyWaybill(driver : String, date : String): Waybill? =
        waybillDao.getDailyWaybill(driver,date)?.let { waybill ->
            val garbageSites = waybillGarbageSiteDao.getAllWithReports(waybill.id).map {
                GarbageSite.fromEntity(it)
            }
            Waybill(
                id = waybill.id.toString(),
                date = waybill.date,
                driver = waybill.driver,
                garbageSites = garbageSites
            )
        }

    override suspend fun fetchDailyWaybill(
        driverFullName: String,
        driverCredentials: DriverCredentials
    ): Either<NetworkError, Waybill> = either {
        Waybill.fromDTO(
            waybillDTO = waybillService.getDayliWaybill(driverCredentials.fullName)
        )
    }

    override suspend fun checkWaybillUpdated(
        waybillID: UUID,
        driverCredentials: DriverCredentials
    ): Either<NetworkError, Boolean> = either {
        waybillService.checkWaybillUpdated(driverCredentials.fullName)
    }

    override suspend fun saveReport(report: Report) {
        reportDao.insertReport(report.toEntity())
    }

    override suspend fun sendReport(
        waybillID: String,
        garbageSiteID: String,
        report: Report
    ): Either<NetworkError, Unit> = either{
        waybillService.sendReport(report.toDTO())
    }
}