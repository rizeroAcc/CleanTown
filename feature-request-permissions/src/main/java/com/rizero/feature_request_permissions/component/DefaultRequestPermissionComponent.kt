package com.rizero.feature_request_permissions.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultCallback
import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Single

class DefaultRequestPermissionComponent(
    componentContext: ComponentContext,
    val androidContext : Context,
    val requiredPermissions : Map<String, String>,
    val onPermissionRequest : (permissions : Array<String>, callback : () -> Unit) -> Unit,
    val onAllPermissionsGranted : () -> Unit,
) : RequestPermissionComponent, ComponentContext by componentContext{
    override val permissionStatus: MutableMap<String, Boolean> = requiredPermissions.mapValues{
        false
    }.toMutableMap()

    val requestPermissionsCallback = {
            updatePermissionStatus(androidContext)
            if (allPermissionsGranted(permissionStatus)){
                onAllPermissionsGranted()
            }
        }

    init {
        updatePermissionStatus(androidContext)
        if (allPermissionsGranted(permissionStatus)){
            onAllPermissionsGranted()
        }
    }

    override fun requestPermissions() {
        val permissionsToRequest = requiredPermissions
            .filter { (uiName, _) -> permissionStatus[uiName] == false }
            .map { it.value } // Manifest.permission
            .toTypedArray()
        onPermissionRequest(
            permissionsToRequest,
            requestPermissionsCallback
        )
    }
    fun checkRequiredPermissions(required: Map<String, String>, context: Context) : Map<String,Boolean>{
        val result = mutableMapOf<String, Boolean>()
        required.forEach { requiredPermission->
            val (permissionName,permission) = requiredPermission
            result[permissionName] = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
        return result
    }

    private fun updatePermissionStatus(context: Context) {
        permissionStatus.putAll(checkRequiredPermissions(
            requiredPermissions,
            context
        ))
    }

    private fun allPermissionsGranted(permissionStatus: MutableMap<String, Boolean>) : Boolean {
        var allGranted = true
        permissionStatus.forEach { (_, granted) ->
            allGranted = allGranted && granted
        }
        return allGranted
    }
}

@Single
class ComponentFactory(
    val requiredPermissions : Map <String, String>,
    val context: Context
) : RequestPermissionComponent.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onPermissionRequest: (permissions: Array<String>, callback: () ->Unit ) -> Unit,
        onAllPermissionsGranted: () -> Unit
    ): RequestPermissionComponent =
        DefaultRequestPermissionComponent(
            androidContext = context,
            requiredPermissions = requiredPermissions,
            componentContext = componentContext,
            onPermissionRequest = onPermissionRequest,
            onAllPermissionsGranted = onAllPermissionsGranted,
        )

}