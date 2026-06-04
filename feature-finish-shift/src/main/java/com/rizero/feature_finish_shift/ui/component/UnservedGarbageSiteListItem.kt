package com.rizero.feature_finish_shift.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_finish_shift.R

@Composable
fun UnservedGarbageSiteListItem(number : Int, address : String, reason : String?, onClick : () -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(60.dp)
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(start = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color =  Color.Gray)
                .size(24.dp)
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = address,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = ("Причина невывоза: ${reason?: "не указана"}"),
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.arrow_forward),
            contentDescription = "Показать на карте",
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(24.dp)
        )
    }
}

@Composable
@Preview
fun UnservedGarbageSiteListItemPreview(){
    Column() {
        UnservedGarbageSiteListItem(1,"Ломоносова 10","Очень очень очень очень длинная причина невывоза") {

        }
        UnservedGarbageSiteListItem(2,"Ломоносова 10",null) {

        }
    }


}