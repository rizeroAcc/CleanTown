package com.rizero.feature_take_photo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_take_photo.R
import com.rizero.shared_ui.AppColors
import kotlinx.coroutines.channels.ticker

@Composable
fun SubmitPhotoScreen(photoLoaded : Boolean){
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(AppColors.defaultBackgroundColor)
            .fillMaxSize()
    ) {
        Text(
            text = "Подтверждение снимка",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        HorizontalDivider()
        if (!photoLoaded){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ){
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(150.dp)
                )
            }
        }else{
            if (imageBitmap != null){
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = "Фото",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                )
            }else{
                //TODO Только для preview потом исправить
                Image(
                    painter = painterResource(R.drawable.img),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Фото",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                )
            }
        }
        Button(
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.buttonBackgroundColor
            ),
            onClick = {},
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(0.6f)
        ) {
            Text("Сохранить")
        }
        Button(shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.lightBackgroundColor
            ),
            onClick = {},
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Переснять")
        }
    }
}

@Composable
@Preview
fun SubmitPhotoScreenLoadingPreview(){
    SubmitPhotoScreen(photoLoaded = false)
}

@Composable
@Preview
fun SubmitPhotoScreenLoadedPreview(){
    SubmitPhotoScreen(photoLoaded = true)
}