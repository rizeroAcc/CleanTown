package com.rizero.feature_uncollect_reason.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.rizero.core_data.model.UncollectedReason
import com.rizero.feature_uncollect_reason.R
import com.rizero.feature_uncollect_reason.component.MockUncollectedReasonComponent
import com.rizero.feature_uncollect_reason.component.UncollectedReasonComponent
import com.rizero.feature_uncollect_reason.store.UncollectedReasonStore
import com.rizero.shared_ui.AppColors

@Composable
fun UncollectedReasonDialog(uncollectedReasonComponent: UncollectedReasonComponent){
    val state by uncollectedReasonComponent.state.collectAsState()
    var notOurReasonsExpanded by remember { mutableStateOf(true) }
    var ourReasonsExpanded by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = AppColors.lightBackgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .fillMaxHeight(0.8f)
            .fillMaxWidth(0.9f)
    ) {
        Text(
            text = "Причина невывоза",
            color = AppColors.defaultTextColor,
            fontSize = 20.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        HorizontalDivider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = {
                    notOurReasonsExpanded = !notOurReasonsExpanded
                })
                .height(40.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Не наша вина",
                color = Color.Yellow,
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )
            Icon(
                imageVector = if (notOurReasonsExpanded)
                    ImageVector.vectorResource(R.drawable.keyboard_arrow_up)
                else
                    ImageVector.vectorResource(R.drawable.keyboard_arrow_down),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp)
            )
        }
        HorizontalDivider()
        AnimatedVisibility(notOurReasonsExpanded) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(state.notOurUncollectedReasons){ item->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable(onClick = {
                                uncollectedReasonComponent.onReasonSelected(item)
                            })
                            .padding(vertical = 4.dp)
                            .heightIn(min = 40.dp)
                    ) {
                        Text(
                            text = item.name,
                            color = AppColors.defaultTextColor,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = {
                    ourReasonsExpanded = !ourReasonsExpanded
                })
                .height(40.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Наша вина",
                color = Color.Red,
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )
            Icon(
                imageVector = if (ourReasonsExpanded)
                    ImageVector.vectorResource(R.drawable.keyboard_arrow_up)
                else
                    ImageVector.vectorResource(R.drawable.keyboard_arrow_down),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp)
            )
        }
        HorizontalDivider()
        AnimatedVisibility(ourReasonsExpanded) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(state.ourUncollectedReasons){ item->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable(onClick = {
                                uncollectedReasonComponent.onReasonSelected(item)
                            })
                            .padding(vertical = 4.dp)
                            .heightIn(min = 40.dp)
                    ) {
                        Text(
                            text = item.name,
                            color = AppColors.defaultTextColor,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
@Preview
fun UncollectedReasonDialogPreview(){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Dialog(onDismissRequest = {

        }) {
            UncollectedReasonDialog(MockUncollectedReasonComponent(
                mockState = UncollectedReasonStore.State(
                    notOurUncollectedReasons = listOf(
                        UncollectedReason(
                            id = 55,
                            name = "Вывозу мешает завал (ТКО/КГО)",
                            our = false
                        ),
                        UncollectedReason(
                            id = 3,
                            name = "Нет доступа к контейнеру (закрыт)",
                            our = false
                        ),
                        UncollectedReason(
                            id = 4,
                            name = "Нет проезда (стояли авто, ремонт дороги)",
                            our = false
                        ),
                        UncollectedReason(
                            id = 15,
                            name = "Отсутствует/неисправен контейнер",
                            our = false
                        ),
                        UncollectedReason(
                            id = 33,
                            name = "Подъездной путь Снег/Лед/Грунт(ТС застрял)",
                            our = false
                        ),
                        UncollectedReason(
                            id = 17,
                            name = "Строительные/Растительные отходы в контейнере",
                            our = false
                        )
                    ),
                    ourUncollectedReasons = listOf(
                        UncollectedReason(
                            id = 52,
                            name = "Без уважительной причины",
                            our = true
                        ),
                        UncollectedReason(
                            id = 44,
                            name = "Переполнена машина",
                            our = true
                        ),
                        UncollectedReason(
                            id = 54,
                            name = "По мед.причинам(снят/болезнь)",
                            our = true
                        ),
                        UncollectedReason(
                            id = 31,
                            name = "По тех.причинам(ремонт)",
                            our = true
                        ),
                        UncollectedReason(
                            id = 53,
                            name = "Произошло ДТП",
                            our = true
                        ),
                        UncollectedReason(
                            id = 11,
                            name = "Снят на другой график",
                            our = true
                        ),
                    )
                )
            ))
        }
    }
}