package com.rizero.core_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "uncollected_reasons"
)
data class UncollectedReasonEntity(
    @PrimaryKey
    val id : Int,
    val name : String,
    val our : Boolean,
    val active : Boolean,
)
