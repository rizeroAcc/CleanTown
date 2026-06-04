package com.rizero.feature_request_permissions.di

import android.Manifest
import android.os.Build
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import org.koin.core.qualifier.named
import org.koin.dsl.module

@Module
@ComponentScan("com.rizero.feature_request_permissions")
@Configuration
class RequestPermissionModule

@Module
@Configuration
class PermissionConfigModule {
    @Single
    fun providePermissionsMap(): Map<String, String> = buildMap {
        put("Доступ к камере", Manifest.permission.CAMERA)
        put("Доступ к текущему местоположению", Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            put("Доступ к внешнему хранилищу", Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}