package com.rizero.core_network

import com.rizero.core_network.dto.GarbageSiteDTO
import com.rizero.core_network.dto.ReportDTO
import com.rizero.core_network.dto.WaybillDTO
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single
import java.time.LocalDate
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
@OptIn(ExperimentalUuidApi::class)
class MockWaybillService : WaybillService{
    private var requestNum = 1
    private val defaultWaybillGs = listOf(
        GarbageSiteDTO(
            id = "01ed592a-fa67-4a7b-94c6-e91060938b81",
            address = "ул. Берёзовая Роща, 57",
            latitude = 51.701506861958606,
            longitude = 39.23097516039235,
        ),
        GarbageSiteDTO(
            id = "c9a646d3-9c61-4cd8-bc11-ea65a74d88e0",
            address = "ул. Кольцовская, 14А",
            latitude = 51.678854,
            longitude = 39.203741,
        ),
        GarbageSiteDTO(
            id = "25bc129e-1e94-4d89-9a25-a1bb40f0fb52",
            address = "пл. Ленина, 1",
            latitude = 51.660066671731066,
            longitude = 39.19876331264794,
        ),
        GarbageSiteDTO(
            id = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            address = "ул. 9 Января, 286",
            latitude = 51.68412687556413,
            longitude = 39.12767440008125,
        )
    )
    override suspend fun getDayliWaybill(driverFullName : String): WaybillDTO {
        delay(2000)
        val date = LocalDate.now()
        return when(requestNum){
            1 -> {
                requestNum++
                WaybillDTO(
                    driver = driverFullName,
                    date = "${date.dayOfMonth}.0${date.monthValue}.${date.year}",
                    number = 6432,
                    garbageSiteList = defaultWaybillGs,
                    id = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                )
            }
            else -> {
                val waybill = WaybillDTO(
                    driver = driverFullName,
                    date = "${date.dayOfMonth}.0${date.monthValue}.${date.year}",
                    number = 6432,
                    garbageSiteList = defaultWaybillGs,
                    id = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                )
                val gsList = waybill.garbageSiteList
                waybill.copy(
                    garbageSiteList = buildList {
                        gsList.forEachIndexed {
                            index, dTO ->
                            if (index != 0){
                                add(dTO)
                            }
                        }
                        add(GarbageSiteDTO(
                            id = "f46ac43b-58cc-4372-a567-0e02b2c3d479",
                            address = "ул. Конструкторов, 1",
                            latitude = 51.666373,
                            longitude = 39.173025
                        ))
                    }
                )
            }
        }
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
