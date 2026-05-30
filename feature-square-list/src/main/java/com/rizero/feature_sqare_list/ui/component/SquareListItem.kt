package com.rizero.feature_sqare_list.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.rizero.feature_sqare_list.R

@Composable
fun SquareListItem(
    number : Int,
    served : Boolean,
    adress : String,
    distance : Int,
    modifier: Modifier = Modifier.height(50.dp)
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(start = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color = if (served) Color.Green else Color.Gray)
                .size(24.dp)
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
            )
        }
        Text(
            text = adress,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.location_on),
                contentDescription = "Показать на карте",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "$distance m",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 12.dp, end = 16.dp)
            )
        }
    }
}


@Composable
@Preview
fun SquareListItemPreview(){
    SquareListItem(1,true,"ул. Ленина 10", 250, modifier = Modifier.height(40.dp))
}