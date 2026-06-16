package com.rizero.feature_sqare_list.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.core_data.model.GarbageSite
import com.rizero.feature_sqare_list.R
import java.util.UUID

@Composable
fun SquareListItem(
    garbageSite: GarbageSite?,
    number : Int,
    modifier: Modifier = Modifier,
    onClick : (GarbageSite) -> Unit,
){
    if (garbageSite == null){
        ShimmerPlaceholder(
            modifier = Modifier
                .then(modifier)
                .height(50.dp)
                .fillMaxWidth()
        )
    }else{
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = { onClick(garbageSite) })
                .then(modifier)
                .height(50.dp)
                .fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = Color.Yellow)
                    .size(24.dp)
            ) {
                Text(
                    text = number.toString(),
                    color = Color.Black,
                )
            }
            Text(
                text = garbageSite.address,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (garbageSite.distanceTo != null){
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.location_on),
                        contentDescription = "Показать на карте",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${garbageSite.distanceTo?: "?"} m",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 16.dp)
                            .widthIn(min = 60.dp)
                    )
                }else{
                    Text(
                        text = "? m",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 16.dp)
                            .widthIn(min = 60.dp)
                    )
                }


            }
        }
    }
}

@Composable
@Preview
fun EmptyGarbageSitePreview(){
    SquareListItem(garbageSite = null, number = 1, modifier = Modifier.padding(top = 100.dp)) { }
}

@Composable
@Preview
fun SquareListItemPreview(){
    Column() {
        SquareListItem(
            garbageSite = GarbageSite(
                id = UUID.randomUUID().toString(),
                address = "Ломоносова 10",
                longitude = 51.252,
                latitude = 32.245,
                distanceTo = 100,
                report = null,
            ),
            number = 1
        ) { }
        SquareListItem(
            garbageSite = GarbageSite(
                id = UUID.randomUUID().toString(),
                address = "Ломоносова 53",
                longitude = 51.252,
                latitude = 32.245,
                distanceTo = null,
                report = null,
            ),
            number = 2
        ) { }
    }
}