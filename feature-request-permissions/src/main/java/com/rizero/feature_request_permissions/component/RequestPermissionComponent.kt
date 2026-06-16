package com.rizero.feature_request_permissions.component

import androidx.activity.result.ActivityResultCallback
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface RequestPermissionComponent {
    val permissionStatus : StateFlow<Map<String, Boolean>>
    fun requestPermissions() : Unit

    fun interface Factory{
        operator fun invoke(
            componentContext : ComponentContext,
            onPermissionRequest: (permissions: Array<String>, callback: () ->Unit ) -> Unit,
            onAllPermissionsGranted : () -> Unit,
        ) : RequestPermissionComponent
    }

}



class MockRequestPermissionComponent(permissionStatus : Map<String, Boolean>? = null) : RequestPermissionComponent{
    override val permissionStatus = MutableStateFlow(permissionStatus?: emptyMap())
    override fun requestPermissions() = Unit
}

