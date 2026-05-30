package com.rizero.feature_trashsite.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_trashsite.R
import com.rizero.shared_ui.AppColors

@Composable
fun UnableToCleanScreen(){
    Box(
        Modifier.fillMaxSize()
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(AppColors.defaultBackgroundColor)
                .fillMaxSize()
        ) {
            Text(
                text = "Мусор не вывезен",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            HorizontalDivider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Причина не вывоза:",
                        fontSize = 16.sp,
                        color = Color.White,

                        )
                    Text(
                        text = "Вывозу мешает завал (ТКО/КГО)",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_forward),
                        contentDescription = "Выбрать причину",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                    )
                }
            }
            HorizontalDivider()
        }
        Button(
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.buttonBackgroundColor
            ),
            onClick = {},
            modifier = Modifier
                .padding(bottom = 24.dp)
                .size(150.dp,50.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Подтвердить"
            )
        }
    }

}

@Composable
@Preview
fun UnableToCleanScreenPreview(){
    UnableToCleanScreen()
}