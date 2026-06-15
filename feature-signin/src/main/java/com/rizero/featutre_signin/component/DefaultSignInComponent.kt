package com.rizero.featutre_signin.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.core_data.model.Driver
import com.rizero.core_data.repository.DriverRepository
import com.rizero.featutre_signin.store.SignInStore
import com.rizero.featutre_signin.store.SignInStoreFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

class DefaultSignInComponent(
    componentContext: ComponentContext,
    val driverRepository: DriverRepository,
    val onAuthorizedCallback : (Driver) -> Unit,
    val storeFactory: StoreFactory = DefaultStoreFactory()
) : SignInComponent, ComponentContext by componentContext {
    val store = instanceKeeper.getStore {
        SignInStoreFactory(
            storeFactory = storeFactory,
            driverRepository = driverRepository
        ).create()
    }
    val scope = coroutineScope()
    override val state = store.stateFlow(lifecycle)
    override fun startAuthorization() {
        store.accept(SignInStore.Intent.AuthorizeDriver)
    }

    override fun changePassword(newValue : String) {
        store.accept(SignInStore.Intent.ChangePassword(newValue))
    }

    override fun changeLogin(newValue : String) {
        store.accept(SignInStore.Intent.ChangeLogin(newValue))
    }

    init {
        scope.launch{
            store.labels.collect { label ->
                when(label){
                    is SignInStore.Label.DriverAuthorized -> onAuthorizedCallback(label.driver)
                }
            }
        }
    }

    @Single
    class Factory(
        val driverRepository: DriverRepository
    ) : SignInComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            onAuthorizedCallback : (Driver) -> Unit,
        ): SignInComponent =
            DefaultSignInComponent(
                componentContext = componentContext,
                onAuthorizedCallback = onAuthorizedCallback,
                driverRepository = driverRepository,
            )

    }
}