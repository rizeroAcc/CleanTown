package com.rizero.core_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "garbage_sites"
)
data class GarbageSite(
    @PrimaryKey
    val guid : UUID,
    val longitude : Double,
    val latitude : Double,
    val address : String,
)