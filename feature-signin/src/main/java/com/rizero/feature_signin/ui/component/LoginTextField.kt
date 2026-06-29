package com.rizero.feature_signin.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.shared_ui.AppColors


@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    supportingTextSize : TextUnit = 12.sp,
) {
    Column(modifier = modifier) {
        Text(
            text = "Логин",
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
                    text = "Введите логин",
                    color = Color(0xFF8A8A8A)
                )
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