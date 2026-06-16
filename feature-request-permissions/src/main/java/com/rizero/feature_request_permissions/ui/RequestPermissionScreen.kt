package com.rizero.feature_request_permissions.ui

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    val permissionStatus by requestPermissionComponent.permissionStatus.collectAsState()
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.dialogBackgroundColor)
            .padding(12.dp)
            .sizeIn(
                minWidth = 200.dp,
                minHeight = 200.dp,
                maxWidth = 320.dp,
                maxHeight = 600.dp,
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Для работы приложения необходимы следующие разрешения. Пожалуйста предоставьте доступ к ним.",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            for ((permissionName, granted) in permissionStatus){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier =
                        Modifier
                            .padding(bottom = 2.dp)
                            .border(width = 1.dp, shape = RoundedCornerShape(8.dp), color = Color.White)
                            .fillMaxWidth()
                ) {
                    Text(
                        text = "$permissionName: ",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier
                            .widthIn(80.dp,200.dp)
                            .padding(start = 16.dp)
                    )
                    Icon(
                        imageVector = if (granted)
                            ImageVector.vectorResource(R.drawable.check_circle)
                        else
                            ImageVector.vectorResource(R.drawable.cross_circle),
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
            "Доступ к камере" to false,
            "Доступ к текущему местоположению" to false
        )
    ))
}