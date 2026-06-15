package com.rizero.core_data.model

import android.net.Uri
import java.util.UUID

data class GarbageSite(
    val guid : String,
    val address : String,
    val longitude : Double,
    val latitude : Double,
    val distanceTo : Int?,
)
