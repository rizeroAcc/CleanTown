package com.rizero.core_data.model

import android.net.Uri

data class Report(
    val id : String,
    val served : Boolean,
    val photoBefore : Uri?,
    val photoAfter : Uri?,
    val uncollectedReason : String?
)
