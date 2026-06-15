package com.rizero.core_data.repository

import com.rizero.core_data.model.UncollectedReason

interface UncollectedReasonRepository {
    suspend fun fetchUncollectedReasons() : List<UncollectedReason>
    suspend fun updateCachedUncollectedReasonList(actualReasons : List<UncollectedReason>)
    suspend fun getAllUncollectedReasons() : List<UncollectedReason>
}