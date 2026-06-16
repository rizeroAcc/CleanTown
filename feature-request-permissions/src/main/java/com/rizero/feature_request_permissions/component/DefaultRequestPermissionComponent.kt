package com.rizero.feature_request_permissions.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultCallback
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

class DefaultRequestPermissionComponent(
    componentContext: ComponentContext,
    val androidContext : Context,
    val requiredPermissions : Map<String, String>,
    val onPermissionRequest : (permissions : Array<String>, callback : () -> Unit) -> Unit,
    val onAllPermissionsGranted : () -> Unit,
) : RequestPermissionComponent, ComponentContext by componentContext{

    private var isRequesting = false
    override val permissionStatus = MutableStateFlow(
        value = requiredPermissions
            .mapValues{
                false
            }
            .toMap()
    )

    val requestPermissionsCallback = {
            isRequesting = false
            updatePermissionStatus(androidContext)
            if (allPermissionsGranted(permissionStatus.value)){
                onAllPermissionsGranted()
            }
        }

    init {
        updatePermissionStatus(androidContext)
        if (allPermissionsGranted(permissionStatus.value)){
            onAllPermissionsGranted()
        }
    }

    override fun requestPermissions() {
        if (isRequesting) return
        val permissionsToRequest = requiredPermissions
            .filter { (uiName, _) -> permissionStatus.value[uiName] == false }
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
        permissionStatus.value = checkRequiredPermissions(
            requiredPermissions,
            context
        )
    }

    private fun allPermissionsGranted(permissionStatus: Map<String, Boolean>) : Boolean {
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