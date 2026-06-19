package com.rizero.feature_sqare_list.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rizero.shared_ui.AppColors

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer animation"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            AppColors.lightBackgroundColor.copy(alpha = 0.6f),
            AppColors.lightBackgroundColor.copy(alpha = 0.9f),
            AppColors.lightBackgroundColor.copy(alpha = 0.6f),
        ),
        start = Offset(x = translateAnim - 300f, y = 0f),
        end = Offset(x = translateAnim, y = 270f),
    )

    Box(
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(brush = brush)
    )
}