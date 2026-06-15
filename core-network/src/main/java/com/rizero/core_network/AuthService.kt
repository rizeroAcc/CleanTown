package com.rizero.core_network

import com.rizero.core_network.dto.DriverCredentialsDTO

interface AuthService {
    suspend fun authorizeDriver(driverCredentialsDTO: DriverCredentialsDTO) : Boolean
}