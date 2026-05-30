package com.rizero.feature_trashsite.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_trashsite.R
import com.rizero.shared_ui.AppColors

@Composable
fun TrashsiteScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(AppColors.defaultBackgroundColor)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_back),
                    contentDescription = "Кнопка назад",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = "Площадка №1",
                    fontSize = 18.sp,
                    color = Color.White,

                )
                Text(
                    text = "ул. Ленина 10",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
        HorizontalDivider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = "Фото до забора мусора",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Image(
                painter = painterResource(R.drawable.no_photo),
                contentDescription = "Фото не сделано",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        brush = SolidColor(AppColors.lightBackgroundColor),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .size(width = 300.dp, height = 180.dp)
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackgroundColor
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .size(200.dp,60.dp)
            ) {
                Text(
                    "Сделать фото",
                    fontSize = 16.sp
                )
            }
            HorizontalDivider()
            Text(
                text = "Фото после забора мусора",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Image(
                painter = painterResource(R.drawable.no_photo),
                contentDescription = "Фото не сделано",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        brush = SolidColor(AppColors.lightBackgroundColor),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .size(width = 300.dp, height = 180.dp)
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackgroundColor
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .size(200.dp,60.dp)
            ) {
                Text(
                    "Сделать фото",
                    fontSize = 16.sp
                )
            }
            HorizontalDivider()
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.lightBackgroundColor
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
            ) {
                Text("Перейти к следующей площадке")
            }
        }
    }
}

@Composable
@Preview
fun TrashsiteScreenPreview(){
     TrashsiteScreen()
}