package com.rabross.clockclock.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun ClocksGridDisplay(
    clocks: List<Pair<Float, Float>>,
    countX: Int,
    countY: Int,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean = true,
    reportPosition: (Int, Offset) -> Unit = { _, _ -> },
    clock: @Composable (hour: Float, minute: Float, modifier: Modifier) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val clockWidth = this.maxWidth / countX
        val clockHeight = this.maxHeight / countY
        val clockSize = clockWidth.coerceAtMost(clockHeight)

        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(countY) { row ->
                Row(
                    modifier = Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(countX) { column ->
                        val index = (row * countX) + column
                        val partClock = clocks[index]
                        val hourHandDegree = partClock.first
                        val minuteHandDegree = partClock.second

                        val hourDegree = animateFloatAsState(
                            targetValue = rememberShortestPathDegree(hourHandDegree),
                            animationSpec = tween(
                                durationMillis = if (shouldAnimate) 800 else 100,
                                easing = if (shouldAnimate) FastOutSlowInEasing else LinearEasing
                            )
                        )

                        val minuteDegree = animateFloatAsState(
                            targetValue = rememberShortestPathDegree(minuteHandDegree),
                            animationSpec = tween(
                                durationMillis = if (shouldAnimate) 800 else 100,
                                easing = if (shouldAnimate) FastOutSlowInEasing else LinearEasing
                            )
                        )

                        clock(
                            hourDegree.value,
                            minuteDegree.value,
                            Modifier
                                .size(clockSize)
                                .onGloballyPositioned { coordinates ->
                                    reportPosition(index, coordinates.boundsInRoot().center)
                                }
                        )
                    }
                }
            }
        }
    }
}