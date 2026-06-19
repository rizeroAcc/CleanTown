package com.rizero.cleantown.di

import com.rizero.core_data.di.DataModule
import com.rizero.core_data.model.UncollectedReason
import com.rizero.core_database.di.DatabaseModule
import com.rizero.core_network.di.NetworkModule
import com.rizero.feature_finish_shift.di.FinishShiftModule
import com.rizero.feature_request_permissions.di.PermissionConfigModule
import com.rizero.feature_request_permissions.di.RequestPermissionModule
import com.rizero.feature_sqare_list.di.SquareListModule
import com.rizero.feature_take_photo.di.TakePhotoModule
import com.rizero.feature_trashsite.di.GarbageSiteModule
import com.rizero.feature_uncollect_reason.di.UncollectedReasonModule
import com.rizero.featutre_signin.di.SignInModule
import org.koin.core.annotation.KoinApplication

@KoinApplication(
    modules = [
        PermissionConfigModule::class,
        RequestPermissionModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        DataModule::class,
        UncollectedReasonModule::class,
        SignInModule::class,
        GarbageSiteModule::class,
        TakePhotoModule::class,
        SquareListModule::class,
        FinishShiftModule::class,
        FlowModule::class,
    ]
)
object KoinInstance