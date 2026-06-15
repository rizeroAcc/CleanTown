package com.rizero.core_network

import com.rizero.core_network.dto.DriverCredentialsDTO
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single

@Single
class MockAuthService : AuthService {
    override suspend fun authorizeDriver(driverCredentialsDTO: DriverCredentialsDTO) : Boolean {
        delay(1500)
        return driverCredentialsDTO.login == "test" && driverCredentialsDTO.password == "123"
    }
}