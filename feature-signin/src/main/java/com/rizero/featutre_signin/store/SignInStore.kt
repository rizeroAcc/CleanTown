package com.rizero.featutre_signin.store

import androidx.compose.material3.Label
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.core_data.model.Driver
import com.rizero.core_data.model.DriverCredentials
import com.rizero.core_data.repository.DriverRepository
import com.rizero.featutre_signin.store.SignInStore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface SignInStore : Store<Intent, State, Label> {
    data class State(
        val login : String = "",
        val password : String = "",
        val authorizing : Boolean = false,
        val error : Error? = null,
    ){
        sealed interface Error{
            data object NoInternetConnection : Error
            data object WrongCredentials : Error
        }
    }
    sealed interface Intent{
        data class ChangeLogin(val newValue: String) : Intent
        data class ChangePassword(val newValue : String) : Intent
        data object AuthorizeDriver : Intent
    }
    sealed interface Label{
        data class DriverAuthorized(val driver: Driver) : Label
    }
}

class SignInStoreFactory(
    val storeFactory : StoreFactory,
    val driverRepository: DriverRepository,
){

    sealed interface Message {
        data class PasswordChanged(val newValue: String) : Message
        data class LoginChanged(val newValue: String) : Message
        data object AuthorizationStarted : Message
        data class ErrorOccured(val error : State.Error) : Message
        data object InitialLoadingFinished : Message
    }

    sealed interface Action {

        data object AuthorizeDriver : Action
        data object ShowInitialLoading : Action
        data object HideInitialLoading : Action
        data class SkipAuthentication(val driver: Driver) : Action
    }

    fun create(): SignInStore =
        object : SignInStore,Store<Intent, State, Label> by storeFactory.create(
            name = "sign in store",
            autoInit = true,
            initialState = State(),
            bootstrapper = SignInStoreBootstrapper(driverRepository),
            executorFactory = { SignInStoreExecutor(driverRepository) },
            reducer = SignInStoreReducer()
        ){

        }

    class SignInStoreBootstrapper(
        val driverRepository: DriverRepository
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch(Dispatchers.Main) {
                dispatch(Action.ShowInitialLoading)
                val driver = withContext(Dispatchers.IO){
                     driverRepository.getSavedDriver()
                }
                if (driver == null){
                    dispatch(Action.HideInitialLoading)
                }else{
                    dispatch(Action.SkipAuthentication(driver))
                }
            }
        }
    }

    class SignInStoreExecutor(val driverRepository: DriverRepository) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {
            when(intent){
                Intent.AuthorizeDriver -> forward(Action.AuthorizeDriver)
                is Intent.ChangeLogin -> dispatch(Message.LoginChanged(intent.newValue))
                is Intent.ChangePassword -> dispatch(Message.PasswordChanged(intent.newValue))
            }
        }
        override fun executeAction(action: Action) {
            when(action){
                Action.HideInitialLoading -> dispatch(Message.InitialLoadingFinished)
                Action.ShowInitialLoading -> dispatch(Message.AuthorizationStarted)
                is Action.SkipAuthentication -> publish(Label.DriverAuthorized(action.driver))
                Action.AuthorizeDriver -> {
                    val state = state()
                    dispatch(Message.AuthorizationStarted)
                    val (login,password) = Pair(state.login,state.password)

                    //todo переделать заглушку
                    if (login == "123" && password == "123"){
                        scope.launch(Dispatchers.IO) {
                            delay(1000)
                            driverRepository.saveDriver(DriverCredentials(login, password))
                            withContext(Dispatchers.Main){
                                publish(Label.DriverAuthorized(Driver(login)))
                            }
                        }
                    }else{
                        dispatch(Message.ErrorOccured(State.Error.WrongCredentials))
                    }

                }
            }
        }


    }

    class SignInStoreReducer() : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when(msg){
                Message.AuthorizationStarted -> copy(authorizing = true)
                is Message.ErrorOccured -> copy(authorizing = false, error = msg.error)
                is Message.LoginChanged -> copy(login = msg.newValue)
                is Message.PasswordChanged -> copy(password = msg.newValue)
                Message.InitialLoadingFinished -> copy(authorizing = false)
        }
    }

}