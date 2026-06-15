package com.rizero.core_network

import com.rizero.core_network.dto.GarbageSiteDTO
import com.rizero.core_network.dto.ReportDTO
import com.rizero.core_network.dto.WaybillDTO
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single
import java.time.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
@OptIn(ExperimentalUuidApi::class)
class MockWaybillService : WaybillService{
    override suspend fun getDayliWaybill(driverFullName : String): WaybillDTO {
        delay(2000)
        val dateTime = LocalDateTime.now()
        return WaybillDTO(
            driver = driverFullName,
            date = "${dateTime.dayOfMonth}.${dateTime.monthValue}.${dateTime.year}",
            number = 6432,
            garbageSiteList = listOf(
                GarbageSiteDTO(
                    guid = Uuid.random().toString(),
                    address = "ул. Берёзовая Роща, 57",
                    latitude = 39.23097516039235,
                    longitude = 51.701506861958606,
                ),
                GarbageSiteDTO(
                    guid = Uuid.random().toString(),
                    address = "ул. Кольцовская, 14А",
                    latitude = 39.203741,
                    longitude = 51.678854
                ),
                GarbageSiteDTO(
                    guid = Uuid.random().toString(),
                    address = "ул. Берёзовая Роща, 57",
                    latitude = 39.23097516039235,
                    longitude = 51.701506861958606,
                ),
                GarbageSiteDTO(
                    guid = Uuid.random().toString(),
                    address = "пл. Ленина, 1",
                    latitude = 39.19876331264794,
                    longitude = 51.660066671731066,
                ),
                GarbageSiteDTO(
                    guid = Uuid.random().toString(),
                    address = "ул. 9 Января, 286",
                    latitude = 39.12767440008125,
                    longitude = 51.68412687556413,
                )

            )
        )
    }

    override suspend fun checkWaybillUpdated(driverFullName : String): Boolean {
        delay(1000)
        return false
    }

    override suspend fun sendReport(reportDTO: ReportDTO) {
        delay(1000)
    }

    override suspend fun attachPhotoToReport(reportID : Uuid) {
        delay(2000)
    }

}
