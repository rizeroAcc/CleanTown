package com.rizero.feature_signin.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.core_data.model.Driver
import com.rizero.feature_signin.store.SignInStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SignInComponent {
    val state : StateFlow<SignInStore.State>
    fun startAuthorization()
    fun changePassword(newValue : String)
    fun changeLogin(newValue : String)
    fun interface Factory{
        operator fun invoke(
            componentContext: ComponentContext,
            onAuthorizedCallback : (Driver) -> Unit,
        ) : SignInComponent
    }
}

class MockSignInComponent(
    val mockState : StateFlow<SignInStore.State>? = null
) : SignInComponent{
    override val state: StateFlow<SignInStore.State>
        get() = mockState ?: MutableStateFlow(SignInStore.State())

    override fun startAuthorization() = Unit
    override fun changePassword(newValue : String) = Unit
    override fun changeLogin(newValue : String) = Unit

}