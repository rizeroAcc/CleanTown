package com.rizero.feature_trashsite.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.core_data.model.GarbageSite
import com.rizero.core_data.model.Report
import com.rizero.feature_trashsite.R
import com.rizero.feature_trashsite.component.GarbageSiteComponent
import com.rizero.feature_trashsite.component.MockGarbageSiteComponent
import com.rizero.feature_trashsite.store.GarbageSiteStore
import com.rizero.feature_uncollect_reason.screen.UncollectedReasonDialog
import com.rizero.shared_ui.AppColors
import java.util.UUID

@Composable
fun GarbageSiteScreen(garbageSiteComponent: GarbageSiteComponent){
    val state by garbageSiteComponent.state.collectAsState()
    val uncollectedReasonDialog by garbageSiteComponent.uncollectedReasonDialog.subscribeAsState()
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
                    garbageSiteComponent.navigateBack()
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
                    text = "Площадка по адресу",
                    fontSize = 18.sp,
                    color = Color.White,

                )
                Text(
                    text = state.garbageSite.address,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
        HorizontalDivider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Мусор вывезен",
                color = AppColors.defaultTextColor,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = state.report.collected,
                onCheckedChange = {
                    if (state.report.uncollectedReason == null) {
                        garbageSiteComponent.changeCollectedStatus()
                    }
                }
            )
        }
        HorizontalDivider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = {
                    garbageSiteComponent.openSelectUncollectedReasonDialog()
                })
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ){
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "Причина невывоза:",
                    color = AppColors.defaultTextColor,
                    fontSize = 14.sp,
                )
                Text(
                    text = state.report.uncollectedReason?.name ?: "Не указана",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                )
            }
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.arrow_forward),
                contentDescription = "Выбрать причину невыаоза",
                tint = Color.White
            )
        }
        HorizontalDivider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Фото до забора мусора",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            if (state.report.photoBefore != null){
                AsyncImage(
                    model = state.report.photoBefore,
                    contentDescription = "Фото площадки до уборки",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            brush = SolidColor(AppColors.lightBackgroundColor),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .size(width = 200.dp, height = 140.dp)
                )
            }else{
                Image(
                    painter = painterResource(R.drawable.no_photo),
                    contentDescription = "Фото не сделано",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            brush = SolidColor(AppColors.lightBackgroundColor),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .size(width = 200.dp, height = 140.dp)
                )
            }
            Button(
                onClick = {
                    garbageSiteComponent.takeBeforePhoto()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackgroundColor
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .size(200.dp, 40.dp)
            ) {
                Text(
                    "Сделать фото",
                    fontSize = 16.sp
                )
            }
            HorizontalDivider()
            Text(
                text = "Фото после забора мусора",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            if (state.report.photoAfter != null){
                AsyncImage(
                    model = state.report.photoAfter,
                    contentDescription = "Фото площадки после уборки",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            brush = SolidColor(AppColors.lightBackgroundColor),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .size(width = 200.dp, height = 140.dp)
                )
            }else{
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
                        .size(width = 200.dp, height = 140.dp)
                )
            }
            Button(
                onClick = {
                    garbageSiteComponent.takeAfterPhoto()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.buttonBackgroundColor
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .size(200.dp, 40.dp)
            ) {
                Text(
                    "Сделать фото",
                    fontSize = 16.sp
                )
            }
            HorizontalDivider()
        }

        Button(
            enabled = state.report.uncollectedReason != null || (
                        state.report.photoBefore != null &&
                        state.report.photoAfter !=null &&
                        state.report.collected
                    )
            ,
            onClick = {
                garbageSiteComponent.navigateBack()
            },
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = AppColors.lightBackgroundColor,
                containerColor = AppColors.buttonBackgroundColor
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth(0.8f)
                .height(50.dp)
        ) {
            Text("Записать данные площадки")
        }
    }
    uncollectedReasonDialog.child?.let { uncollectedReasonDialog->
        Dialog(
            onDismissRequest = {}
        ){
            UncollectedReasonDialog(uncollectedReasonDialog.instance)
        }
    }
}

@Composable
@Preview
fun GarbageSiteScreenPreview(){
     GarbageSiteScreen(MockGarbageSiteComponent(GarbageSiteStore.State(
         garbageSite = GarbageSite(
             id = UUID.randomUUID().toString(),
             address = "Ломоносова 10",
             longitude = 51.432532,
             latitude = 32.43252,
             distanceTo = 1000,
             report = null
         ),
         report = Report(
             id = UUID.randomUUID(),
             garbageSiteID = UUID.randomUUID(),
             collected = false,
             photoBefore = null,
             photoAfter = null,
             uncollectedReason = null
         )
     )))
}