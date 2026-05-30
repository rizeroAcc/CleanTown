package com.rizero.feature_request_permissions.component

import androidx.activity.result.ActivityResultCallback
import com.arkivanov.decompose.ComponentContext

interface RequestPermissionComponent {
    val permissionStatus : Map<String, Boolean>
    fun requestPermissions() : Unit

    fun interface Factory{
        operator fun invoke(
            componentContext : ComponentContext,
            onPermissionRequest: (permissions: Array<String>, callback: () ->Unit ) -> Unit,
            onAllPermissionsGranted : () -> Unit,
        ) : RequestPermissionComponent
    }

}



class MockRequestPermissionComponent(override val permissionStatus: Map<String, Boolean>) : RequestPermissionComponent{
    override fun requestPermissions() = Unit
}

