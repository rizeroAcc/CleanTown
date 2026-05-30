package com.rizero.featutre_signin.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.featutre_signin.R
import com.rizero.featutre_signin.ui.component.LoginTextField
import com.rizero.featutre_signin.ui.component.PasswordTextField
import com.rizero.shared_ui.AppColors

@Composable
fun SignInScreen(){
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
            contentScale = ContentScale.FillBounds,
            alpha = 0.9f,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(180.dp)
        )
        var login by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        LoginTextField(
            value = login,
            onValueChange = {
                login = it
            },
            supportingTextSize = 16.sp,
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp)
                .fillMaxWidth()
        )
        PasswordTextField(
            value = password,
            onValueChange = {
                password = it
            },
            supportingTextSize = 16.sp,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .height(80.dp)
                .fillMaxWidth()
        )

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.buttonBackgroundColor
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
    SignInScreen()
}