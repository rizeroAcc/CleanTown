package com.rizero.cleantown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.arkivanov.decompose.defaultComponentContext
import com.rizero.cleantown.di.KoinInstance
import com.rizero.cleantown.component.RootComponent
import com.rizero.cleantown.component.ShiftFlowComponent
import com.rizero.cleantown.ui.screen.RootScreen
import com.rizero.cleantown.ui.theme.CleanTownTheme
import com.rizero.feature_request_permissions.component.RequestPermissionComponent
import com.rizero.featutre_signin.component.SignInComponent
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.plugin.module.dsl.startKoin



class MainActivity : ComponentActivity() {

    var permissionRequestCallback : Function0<Unit>? = null
    val permissionRequestLauncher = registerForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ){
        permissionRequestCallback?.let {
            it()
        }
    }
    private fun launchPermissions(permissions: Array<String>) {
        if (permissions.isNotEmpty()) {
            permissionRequestLauncher.launch(permissions)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        startKoin<KoinInstance> {
            androidContext(this@MainActivity)
        }
        val requestPermissionComponentFactory : RequestPermissionComponent.Factory by inject()
        val signInComponentFactory : SignInComponent.Factory by inject()
        val shiftFlowComponentFactory : ShiftFlowComponent.Factory by inject()
        val rootComponent = RootComponent(
            componentContext = defaultComponentContext(),
            requestPermissionComponentFactory = requestPermissionComponentFactory,
            shiftFlowComponentFactory = shiftFlowComponentFactory,
            signInComponentFactory = signInComponentFactory,
            onPermissionRequest = { permissions, callback ->
                permissionRequestCallback = callback
                launchPermissions(permissions)
            },
        )
        enableEdgeToEdge()
        setContent {
            CleanTownTheme {
                RootScreen(rootComponent)
            }
        }
    }
}

