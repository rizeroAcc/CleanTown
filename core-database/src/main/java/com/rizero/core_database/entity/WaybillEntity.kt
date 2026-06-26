package com.rizero.core_database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "waybills",
    indices = [Index(value = ["driver", "date"])]
)
data class WaybillEntity(
    @PrimaryKey
    val id : UUID,
    val driver : String,
    val date : String,
    val updateTime : String,
)