package com.rizero.core_data.model

import com.rizero.core_network.dto.DriverCredentialsDTO

data class DriverCredentials(
    val fullName : String,
    val password : String,
) {

}

fun DriverCredentials.toDto() : DriverCredentialsDTO {
    return DriverCredentialsDTO(
        login = this.fullName,
        password = this.password
    )
}