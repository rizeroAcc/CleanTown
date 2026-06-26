package com.rizero.feature_sqare_list.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.core_data.model.GarbageSite
import com.rizero.feature_sqare_list.R

@Composable
fun WaybillSitesList(
    nearestGarbageSiteList : List<GarbageSite>?,
    garbageSiteList : List<GarbageSite>,
    nearestSquaresExpanded : Boolean = false,
    allSquaresExpanded : Boolean = true,
    onGarbageSiteSelected : (GarbageSite)->Unit
){

    var nearestSquaresExpanded by remember { mutableStateOf(nearestSquaresExpanded) }
    var allSquaresExpanded by remember { mutableStateOf(allSquaresExpanded) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = {
                nearestSquaresExpanded = !nearestSquaresExpanded
            })
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Ближайшие площадки (${nearestGarbageSiteList?.size?:"?"})",
            color = Color.White,
            fontSize = 16.sp
        )
        Image(
            imageVector = if (nearestSquaresExpanded)
                ImageVector.vectorResource(R.drawable.keyboard_arrow_up)
            else
                ImageVector.vectorResource(R.drawable.keyboard_arrow_down),
            contentDescription = "Показать ближайшие",
            modifier = Modifier.size(30.dp)
        )
    }
    HorizontalDivider()
    AnimatedVisibility(
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        visible = nearestSquaresExpanded,
    ) {
        LazyColumn() {
            if (nearestGarbageSiteList != null){
                itemsIndexed(items = nearestGarbageSiteList){ index,item->
                    SquareListItem(
                        garbageSite = item,
                        number = index + 1,
                    ) {
                        onGarbageSiteSelected(item)
                    }
                    HorizontalDivider()
                }
            }
            else{
                items(3){
                    SquareListItem(
                        garbageSite = null, number = -1
                    ) {

                    }
                    HorizontalDivider()
                }
            }

        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(
                onClick = {
                    allSquaresExpanded = !allSquaresExpanded
                }
            )
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Все площадки (${garbageSiteList.size})",
            color = Color.White,
            fontSize = 16.sp
        )
        Image(
            imageVector = if (allSquaresExpanded)
                ImageVector.vectorResource(R.drawable.keyboard_arrow_up)
            else
                ImageVector.vectorResource(R.drawable.keyboard_arrow_down),
            contentDescription = "Показать все площадки",
            modifier = Modifier.size(30.dp)
        )
    }
    HorizontalDivider()

    AnimatedVisibility(
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        visible = allSquaresExpanded,
    ) {
        LazyColumn() {
            itemsIndexed(items = garbageSiteList){ index,item->
                SquareListItem(
                    garbageSite = item,
                    number = index + 1,
                ) {
                    onGarbageSiteSelected(item)
                }
                HorizontalDivider()
            }
        }
    }
}