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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.rizero.core_data.model.UncollectedReason
import com.rizero.feature_uncollect_reason.R
import com.rizero.feature_uncollect_reason.component.MockUncollectedReasonComponent
import com.rizero.feature_uncollect_reason.component.UncollectedReasonComponent
import com.rizero.feature_uncollect_reason.screen.component.AccordionHeader
import com.rizero.feature_uncollect_reason.screen.component.ReasonItem
import com.rizero.feature_uncollect_reason.store.UncollectedReasonStore
import com.rizero.shared_ui.AppColors

@Composable
fun UncollectedReasonDialog(uncollectedReasonComponent: UncollectedReasonComponent) {
    val state by uncollectedReasonComponent.state.collectAsState()

    var notOurReasonsExpanded by remember { mutableStateOf(true) }
    var ourReasonsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(AppColors.lightBackgroundColor, RoundedCornerShape(12.dp))
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.85f)           // главное — ограничить высоту
    ) {
        // Заголовок
        Text(
            text = "Причина невывоза",
            color = AppColors.defaultTextColor,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        HorizontalDivider()

        // Контейнер для списков (самое важное изменение)
        Column(
            modifier = Modifier
                .weight(1f)           // занимает всё доступное пространство
                .fillMaxWidth()
        ) {
            // Первый список
            AccordionHeader(
                title = "Не наша вина",
                color = Color.Yellow,
                expanded = notOurReasonsExpanded,
                onClick = { notOurReasonsExpanded = !notOurReasonsExpanded }
            )

            AnimatedVisibility(notOurReasonsExpanded) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)   // важно: fill = false
                ) {
                    items(state.notOurUncollectedReasons) { item ->
                        ReasonItem(item) { uncollectedReasonComponent.onReasonSelected(item) }
                        HorizontalDivider()
                    }
                }
            }

            HorizontalDivider()

            // Второй список
            AccordionHeader(
                title = "Наша вина",
                color = Color.Red,
                expanded = ourReasonsExpanded,
                onClick = { ourReasonsExpanded = !ourReasonsExpanded }
            )

            AnimatedVisibility(ourReasonsExpanded) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(state.ourUncollectedReasons) { item ->
                        ReasonItem(item) { uncollectedReasonComponent.onReasonSelected(item) }
                        HorizontalDivider()
                    }
                }
            }
        }

        // Кнопка всегда внизу
        Button(
            onClick = { uncollectedReasonComponent.removeUncollectedReason() },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.buttonBackgroundColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Убрать причину невывоза")
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