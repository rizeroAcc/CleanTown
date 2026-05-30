package com.rizero.cleantown

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import com.rizero.cleantown.ui.component.RootComponent
import com.rizero.cleantown.ui.screen.RootScreen
import com.rizero.cleantown.ui.theme.CleanTownTheme
import com.rizero.feature_request_permissions.component.RequestPermissionComponent
import com.rizero.feature_request_permissions.di.PermissionConfigModule
import com.rizero.feature_request_permissions.di.RequestPermissionModule
import com.rizero.feature_sqare_list.ui.screen.SelectedSquareDisplay
import com.rizero.feature_sqare_list.ui.screen.SquareListScree
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(
    modules = [
        PermissionConfigModule::class,
        RequestPermissionModule::class,
    ]
)
object KoinInstance

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var permissionRequestCallback : Function0<Unit>? = null
        val permissionRequestLauncher = registerForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ){
            permissionRequestCallback?.let {
                it()
            }
        }
        super.onCreate(savedInstanceState)
        startKoin<KoinInstance> {
            androidContext(this@MainActivity)
        }
        val requestPermissionComponentFactory : RequestPermissionComponent.Factory by inject()
        val rootComponent = RootComponent(
            componentContext = defaultComponentContext(),
            requestPermissionComponentFactory = requestPermissionComponentFactory,
            onPermissionRequest = { permissions, callback ->
                permissionRequestCallback = callback
                permissionRequestLauncher.launch(permissions)
            }
        )
        enableEdgeToEdge()
        setContent {
            CleanTownTheme {
                RootScreen(rootComponent)
//                SquareListScree(true, SelectedSquareDisplay.MAP)
            }
        }
    }
}

