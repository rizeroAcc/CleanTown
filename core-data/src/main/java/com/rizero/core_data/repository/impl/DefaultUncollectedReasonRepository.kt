package com.rizero.core_data.repository.impl

import com.rizero.core_data.model.UncollectedReason
import com.rizero.core_data.repository.UncollectedReasonRepository
import com.rizero.core_database.dao.UncollectedReasonDao
import com.rizero.core_database.entity.UncollectedReasonEntity
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single

@Single
class DefaultUncollectedReasonRepository(
    val uncollectedReasonDao: UncollectedReasonDao,
) : UncollectedReasonRepository{
    override suspend fun fetchUncollectedReasons(): List<UncollectedReason> {
        delay(1500)
        return listOf(
            UncollectedReason(
                id = 52,
                name = "Без уважительной причины",
                our = true
            ),
            UncollectedReason(
                id = 44,
                name = "Переполнена машина",
                our = true
            ),
            UncollectedReason(
                id = 54,
                name = "По мед.причинам(снят/болезнь)",
                our = true
            ),
            UncollectedReason(
                id = 31,
                name = "По тех.причинам(ремонт)",
                our = true
            ),
            UncollectedReason(
                id = 53,
                name = "Произошло ДТП",
                our = true
            ),
            UncollectedReason(
                id = 11,
                name = "Снят на другой график",
                our = true
            ),
            UncollectedReason(
                id = 55,
                name = "Вывозу мешает завал (ТКО/КГО)",
                our = false
            ),
            UncollectedReason(
                id = 3,
                name = "Нет доступа к контейнеру (закрыт)",
                our = false
            ),
            UncollectedReason(
                id = 4,
                name = "Нет проезда (стояли авто, ремонт дороги)",
                our = false
            ),
            UncollectedReason(
                id = 15,
                name = "Отсутствует/неисправен контейнер",
                our = false
            ),
            UncollectedReason(
                id = 33,
                name = "Подъездной путь Снег/Лед/Грунт(ТС застрял)",
                our = false
            ),
            UncollectedReason(
                id = 17,
                name = "Строительные/Растительные отходы в контейнере",
                our = false
            ),
        )
    }

    override suspend fun updateCachedUncollectedReasonList(actualReasons : List<UncollectedReason>) {
        val cachedReasonsID = getAllUncollectedReasons().map { it.id }
        val actualReasonsID = actualReasons.map { it.id }
        uncollectedReasonDao.insertAll(actualReasons.map {
            UncollectedReasonEntity(
                id = it.id,
                name = it.name,
                our = it.our,
                active = true
            )
        })
        for (reasonID in cachedReasonsID){
            if (!actualReasonsID.contains(reasonID)){
                uncollectedReasonDao.deactivateUncollectedReason(reasonID)
            }
        }
    }

    override suspend fun getAllUncollectedReasons(): List<UncollectedReason> {
        return uncollectedReasonDao.getAllActiveReasons().map {
            UncollectedReason(
                id = it.id,
                name = it.name,
                our = it.our
            )
        }
    }

}
