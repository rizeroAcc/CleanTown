package com.rizero.featutre_signin.component

import com.arkivanov.decompose.ComponentContext
import org.koin.core.annotation.Single

class DefaultSignInComponent(
    componentContext: ComponentContext,
    val onAuthorizedCallback : () -> Unit,
) : SignInComponent, ComponentContext by componentContext {
    override fun onAuthorized() {
        onAuthorizedCallback()
    }

    @Single
    class Factory : SignInComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            onAuthorizedCallback : () -> Unit,
        ): SignInComponent =
            DefaultSignInComponent(
                componentContext = componentContext,
                onAuthorizedCallback = onAuthorizedCallback
            )

    }
}