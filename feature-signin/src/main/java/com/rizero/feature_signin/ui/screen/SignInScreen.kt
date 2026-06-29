package com.rizero.feature_signin.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_signin.R
import com.rizero.feature_signin.component.MockSignInComponent
import com.rizero.feature_signin.component.SignInComponent
import com.rizero.feature_signin.store.SignInStore
import com.rizero.feature_signin.ui.component.LoginTextField
import com.rizero.feature_signin.ui.component.PasswordTextField
import com.rizero.shared_ui.AppColors
import kotlinx.coroutines.flow.MutableStateFlow
@Composable
fun SignInScreen(signInComponent: SignInComponent){
    val state by signInComponent.state.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(AppColors.defaultBackgroundColor)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.double_leaf),
            contentDescription = "leaf logo",
            modifier = Modifier
                .padding(top = 32.dp)
                .size(60.dp)
        )
        Text(
            text = stringResource(R.string.AppName),
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 16.dp)
        )
        Image(
            painter = painterResource(R.drawable.truck_logo),
            contentDescription = "truck logo",
            contentScale = ContentScale.Crop,
            alpha = 0.9f,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(180.dp)
        )

        LoginTextField(
            value = state.login,
            onValueChange = { newLogin->
                signInComponent.changeLogin(newLogin)
            },
            supportingTextSize = 16.sp,
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp)
                .fillMaxWidth()
        )
        PasswordTextField(
            value = state.password,
            onValueChange = { newPassword->
                signInComponent.changePassword(newPassword)
            },
            supportingTextSize = 16.sp,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .height(80.dp)
                .fillMaxWidth()
        )

        if (state.error !=  null){
            Text(
                text = when(state.error!!){
                    SignInStore.State.Error.NoInternetConnection -> "Нет соединения с интернетом"
                    SignInStore.State.Error.WrongCredentials -> "Неверный логин или пароль"
                },
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            enabled = !state.authorizing,
            onClick = {
                signInComponent.startAuthorization()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.buttonBackgroundColor,
                disabledContainerColor = AppColors.lightBackgroundColor
            ),
            modifier = Modifier
                .padding(24.dp)
                .height(48.dp)
                .fillMaxWidth(0.6f)
        ) {
            Text(text = "Войти")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SingInDefaultPreview(){
    SignInScreen(MockSignInComponent())
}

@Composable
@Preview(showBackground = true)
fun SingInErrPreview(){
    SignInScreen(MockSignInComponent(mockState = MutableStateFlow(SignInStore.State(
        error = SignInStore.State.Error.WrongCredentials
    ))))
}