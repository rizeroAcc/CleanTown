package com.rizero.featutre_signin.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.featutre_signin.R
import com.rizero.shared_ui.AppColors

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    supportingTextSize : TextUnit = 12.sp,
) {

    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = "Пароль",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontSize = supportingTextSize,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "Введите пароль",
                    color = Color(0xFF8A8A8A)
                )
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            ImageVector.vectorResource(R.drawable.visibility_off)
                        else
                            ImageVector.vectorResource(R.drawable.visibility_visible),
                        contentDescription = if (passwordVisible)
                            "Скрыть пароль"
                        else
                            "Показать пароль",
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = AppColors.textFieldBackgroundColor,
                focusedContainerColor = AppColors.textFieldBackgroundColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxSize()
        )
    }
}