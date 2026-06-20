package com.rizero.feature_uncollect_reason.screen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_uncollect_reason.R

@Composable
fun AccordionHeader(
    title: String,
    color: Color,
    expanded: Boolean,
    onClick: () -> Unit
) {
    HorizontalDivider()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(48.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            color = color,
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )
        Icon(
            imageVector = if (expanded)
                ImageVector.vectorResource(R.drawable.keyboard_arrow_up)
            else
                ImageVector.vectorResource(R.drawable.keyboard_arrow_down),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(32.dp)
        )
    }
    HorizontalDivider()
}