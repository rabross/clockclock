package com.rabross.clockclock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rabross.clockclock.ui.models.Number
import com.rabross.clockclock.ui.models.SixPartClock
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.Calendar
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Composable
fun SixPartClockDisplay(
    number: Number,
    modifier: Modifier = Modifier
) {
    SixPartClockDisplay(
        number.partClocks,
        modifier
    )
}

@Composable
fun SixPartClockDisplay(
    partClocks: SixPartClock,
    modifier: Modifier = Modifier
) {
    val rowSize = 2
    val columnSize = 3
    BoxWithConstraints(modifier) {
        val clockWidth = this.maxWidth / rowSize
        val clockHeight = this.maxHeight / columnSize
        val clockSize = clockWidth.coerceAtMost(clockHeight)
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            repeat(columnSize) { row ->
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    repeat(rowSize) { column ->
                        val partClock = partClocks[(row * rowSize) + column]
                        PartClock(
                            partClock.first,
                            partClock.second,
                            modifier = Modifier.size(clockSize)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SixPartClockDisplayRow(modifier: Modifier = Modifier, digits: Pair<Int, Int>) {
    Row(horizontalArrangement = Arrangement.Start) {
        digits.reverseMap {
            SixPartClockDisplay(
                Number.map(it),
                modifier
            )
        }
    }
}

private inline fun <reified T> Pair<T, T>.reverseMap(block: (value: T) -> Unit) {
    block(second)
    block(first)
}

@Preview
@Composable
private fun SixPartClockPreview() {
    Surface(modifier = Modifier.background(color = Color.White)) {
        SixPartClockDisplay(
            arrayOf(
                randomAngle to randomAngle, randomAngle to randomAngle,
                randomAngle to randomAngle, randomAngle to randomAngle,
                randomAngle to randomAngle, randomAngle to randomAngle
            ),
            Modifier
        )
    }
}

@Preview
@Composable
private fun OnetoNinePreview() {
    Surface(modifier = Modifier.background(color = Color.White)) {
        BoxWithConstraints {
            val clockWidth = this.maxWidth / 3
            val clockHeight = this.maxHeight / 3
            Column(verticalArrangement = Arrangement.Top) {
                Row(horizontalArrangement = Arrangement.Start) {
                    val numbers = listOf(Number.One, Number.Two, Number.Three)
                    numbers.forEach {
                        SixPartClockDisplay(
                            it,
                            Modifier
                                .requiredWidthIn(0.dp, clockWidth)
                                .requiredHeightIn(0.dp, clockHeight)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    val numbers = listOf(Number.Four, Number.Five, Number.Six)
                    numbers.forEach {
                        SixPartClockDisplay(
                            it,
                            Modifier
                                .requiredWidthIn(0.dp, clockWidth)
                                .requiredHeightIn(0.dp, clockHeight)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    val numbers = listOf(Number.Seven, Number.Eight, Number.Nine)
                    numbers.forEach {
                        SixPartClockDisplay(
                            it,
                            Modifier
                                .requiredWidthIn(0.dp, clockWidth)
                                .requiredHeightIn(0.dp, clockHeight)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.Blank,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                    SixPartClockDisplay(
                        Number.Zero,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                    SixPartClockDisplay(
                        Number.Blank,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                }
            }
        }
    }
}

@ExperimentalTime
@Preview
@Composable
private fun SixPartClockTimePreview() {
    Surface(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
    ) {

        val hour = remember { mutableIntStateOf(-1) }
        val minute = remember { mutableIntStateOf(-1) }
        val second = remember { mutableIntStateOf(-1) }

        LaunchedEffect(Unit) {
            tickerFlow(1.seconds, 1.seconds)
                .map { Calendar.getInstance() }
                .distinctUntilChanged { old, new ->
                    old.get(Calendar.SECOND) == new.get(Calendar.SECOND)
                }
                .onEach { calendar ->
                    hour.intValue = calendar.get(Calendar.HOUR_OF_DAY)
                    minute.intValue = calendar.get(Calendar.MINUTE)
                    second.intValue = calendar.get(Calendar.SECOND)
                }
                .launchIn(this)
        }

        BoxWithConstraints {
            val sixClockWidth = this.maxWidth / 2
            val sixClockHeight = this.maxHeight / 3
            Column(verticalArrangement = Arrangement.Top) {
                SixPartClockDisplayRow(
                    Modifier
                        .requiredWidthIn(0.dp, sixClockWidth)
                        .requiredHeightIn(0.dp, sixClockHeight),
                    digits = hour.intValue.twoRightMostDigits()
                )
                SixPartClockDisplayRow(
                    Modifier
                        .requiredWidthIn(0.dp, sixClockWidth)
                        .requiredHeightIn(0.dp, sixClockHeight),
                    digits = minute.intValue.twoRightMostDigits()
                )
                SixPartClockDisplayRow(
                    Modifier
                        .requiredWidthIn(0.dp, sixClockWidth)
                        .requiredHeightIn(0.dp, sixClockHeight),
                    digits = second.intValue.twoRightMostDigits()
                )
            }
        }
    }
}