package com.rizero.featutre_signin.component

import com.arkivanov.decompose.ComponentContext

interface SignInComponent {
    fun onAuthorized()
    fun interface Factory{
        operator fun invoke(
            componentContext: ComponentContext,
            onAuthorizedCallback : () -> Unit,
        ) : SignInComponent
    }
}

class MockSignInComponent() : SignInComponent{
    override fun onAuthorized() = Unit
}