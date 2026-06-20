package com.rizero.feature_uncollect_reason.screen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.core_data.model.UncollectedReason
import com.rizero.shared_ui.AppColors

@Composable
fun ReasonItem(item: UncollectedReason, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .heightIn(min = 44.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = item.name,
            color = AppColors.defaultTextColor,
            fontSize = 16.sp
        )
    }
}