package com.rizero.feature_request_permissions.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_request_permissions.R
import com.rizero.feature_request_permissions.component.MockRequestPermissionComponent
import com.rizero.feature_request_permissions.component.RequestPermissionComponent
import com.rizero.shared_ui.AppColors

@Composable
fun RequestPermissionScreen(requestPermissionComponent: RequestPermissionComponent){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Для работы приложения необходимы следующие разрешения. Пожалуйста предоставьте доступ к ним.",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            for ((permissionName, granted) in requestPermissionComponent.permissionStatus){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier =
                        Modifier
                            .border(width = 1.dp, shape = RectangleShape, color = Color.White)
                            .fillMaxWidth()
                ) {
                    Text(
                        text = "$permissionName: ",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Icon(
                        imageVector = if (granted)
                            ImageVector.vectorResource(R.drawable.check)
                        else
                            ImageVector.vectorResource(R.drawable.cross),
                        contentDescription = if (granted)
                            "Доступ предоставлен"
                        else
                            "Нет доступа",
                        tint = if (granted) Color.Green else Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Button(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackgroundColor
                ),
                onClick = {
                    requestPermissionComponent.requestPermissions()
                },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text(
                    text = "Предоставить разрешения",
                )
            }
        }
    }
}

@Composable
@Preview
fun RequestPermissionScreenPreview(){
    RequestPermissionScreen(MockRequestPermissionComponent(
        mapOf(
            "Доступ к хранилищу" to true,
            "Доступ к камере" to false
        )
    ))
}