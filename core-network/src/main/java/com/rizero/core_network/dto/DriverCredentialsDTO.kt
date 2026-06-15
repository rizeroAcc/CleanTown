package com.rizero.core_network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DriverCredentialsDTO(
    val login : String,
    val password : String,
)
