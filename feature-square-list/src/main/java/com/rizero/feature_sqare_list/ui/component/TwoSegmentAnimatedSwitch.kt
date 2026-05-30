package com.rizero.feature_sqare_list.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.rizero.feature_sqare_list.ui.screen.SelectedSquareDisplay

enum class TwoSegmentAnimatedSwitchPosition{
    LEFT,
    RIGHT,
}

@Composable
fun TwoSegmentAnimatedSwitch(
    selected : TwoSegmentAnimatedSwitchPosition,
    leftContent : @Composable ()-> Unit,
    rightContent : @Composable ()-> Unit,
    onLeftClick : () -> Unit,
    onRightClick : () -> Unit,
    modifier: Modifier = Modifier,
){
    val density = LocalDensity.current
    val width = LocalConfiguration.current.screenWidthDp
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier.clickable(onClick = {
                    onLeftClick()
                })
            ) {
                leftContent()
            }
            Box(
                modifier = Modifier.clickable(onClick = {
                    onRightClick()
                })
            ) {
                rightContent()
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            val indicatorOffset by animateFloatAsState(
                targetValue = if (selected == TwoSegmentAnimatedSwitchPosition.LEFT) 0f else 0.5f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "tab indicator"
            )
            HorizontalDivider(Modifier.padding(top = 1.dp))
            HorizontalDivider(
                thickness = 3.dp,
                color = Color.Green,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .offset(x = indicatorOffset * with(density){1.dp} * width) // смещение
            )
        }
    }
}