package com.rizero.core_data.model

import com.rizero.core_database.entity.UncollectedReasonEntity

data class UncollectedReason(
    val id : Int,
    val name : String,
    val our : Boolean
){
    companion object {
        fun fromEntity(uncollectedReasonEntity: UncollectedReasonEntity) = UncollectedReason(
            id = uncollectedReasonEntity.id,
            name = uncollectedReasonEntity.name,
            our = uncollectedReasonEntity.our
        )
    }
}
