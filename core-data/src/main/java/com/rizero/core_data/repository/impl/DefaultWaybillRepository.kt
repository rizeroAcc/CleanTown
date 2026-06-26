package com.rizero.core_data.repository.impl

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
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
import com.rizero.core_database.WaybillsDatabase
import com.rizero.core_database.dao.ReportDao
import com.rizero.core_database.dao.WaybillDao
import com.rizero.core_database.dao.WaybillGarbageSiteDao
import com.rizero.core_network.WaybillService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.time.LocalDateTime
import java.util.UUID

@Single
class DefaultWaybillRepository(
    val waybillService: WaybillService,
    val waybillDao: WaybillDao,
    val waybillGarbageSiteDao: WaybillGarbageSiteDao,
    val reportDao: ReportDao,
    val database: WaybillsDatabase,
) : WaybillRepository {

    override suspend fun getLocalDailyWaybillUpdates(driver : String, date : String): Flow<Waybill?> =
       waybillDao.getDailyWaybillUpdates(driver,date).map { waybill ->
            if (waybill == null) return@map null
            val garbageSites = waybillGarbageSiteDao.getAllWithReports(waybill.id).map {
                GarbageSite.fromEntity(it)
            }
            Waybill(
                id = waybill.id.toString(),
                date = waybill.date,
                driver = waybill.driver,
                garbageSites = garbageSites,
                updateTime = LocalDateTime.parse(waybill.updateTime)
            )
        }

    override suspend fun getLocalDailyWaybill(
        driver: String,
        date: String
    ): Waybill? {
        val waybillEntity = waybillDao.getDailyWaybill(driver,date) ?: return null
        val garbageSites = waybillGarbageSiteDao.getAllWithReports(waybillEntity.id).map {
            GarbageSite.fromEntity(it)
        }
        return Waybill(
            id = waybillEntity.id.toString(),
            date = waybillEntity.date,
            driver = waybillEntity.driver,
            garbageSites = garbageSites,
            updateTime = LocalDateTime.parse(waybillEntity.updateTime)
        )
    }

    override suspend fun saveOrUpdateWaybill(newWaybill: Waybill, currentWaybillID : UUID?) {
        val savedGarbageSites = currentWaybillID?.let { waybillGarbageSiteDao.getAllWithReports(currentWaybillID) }

        val waybillID = UUID.fromString(newWaybill.id)
        val waybillEntity = newWaybill.toEntity()
        val garbageSites = newWaybill.garbageSites.map { it.toEntity(waybillID) }

        if (savedGarbageSites != null){
            val newGarbageSitesUUID = newWaybill.garbageSites.map { UUID.fromString(it.id) }
            val garbageSiteToDelete = savedGarbageSites.filter { !newGarbageSitesUUID.contains(it.garbageSite.id) }.map { it.garbageSite }

            database.useWriterConnection{ transactor ->
                transactor.immediateTransaction {
                    waybillGarbageSiteDao.deleteWaybillGarbageSiteList(garbageSiteToDelete)
                    waybillDao.insertWaybill(waybillEntity)
                    waybillGarbageSiteDao.insertWaybillGarbageSiteList(garbageSites)
                }
            }

        }else{
            database.useWriterConnection{ transactor ->
                transactor.immediateTransaction {
                    waybillDao.insertWaybill(waybillEntity)
                    waybillGarbageSiteDao.insertWaybillGarbageSiteList(garbageSites)
                }
            }
        }

    }


    override suspend fun fetchDailyWaybill(
        driverFullName: String,
        driverCredentials: DriverCredentials
    ): Either<NetworkError, Waybill> = either {
        Waybill.fromDTO(
            waybillDTO = waybillService.getDayliWaybill(driverFullName)
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