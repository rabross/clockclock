package com.rabross.clockclock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rabross.clockclock.ui.models.Number
import com.rabross.clockclock.ui.models.SixPartClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Composable
fun SixPartClockDisplay(
    number: Number,
    modifier: Modifier = Modifier
) {
    SixPartClockDisplay(
        number.partClocks,
        modifier)
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
            (0 until columnSize).forEach { row ->
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    (0 until rowSize).forEach { column ->
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

private inline fun <reified T> Pair<T,T>.reverseMap(block: (value: T) -> Unit){
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
                randomAngle to randomAngle, randomAngle to randomAngle),
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
                    SixPartClockDisplay(
                        Number.One,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                    SixPartClockDisplay(
                        Number.Two,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                    SixPartClockDisplay(
                        Number.Three,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.Four,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                    SixPartClockDisplay(
                        Number.Five,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                    SixPartClockDisplay(
                        Number.Six,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.Seven,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                    SixPartClockDisplay(
                        Number.Eight,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
                    SixPartClockDisplay(
                        Number.Nine,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight)
                    )
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
    Surface(modifier = Modifier
        .background(color = Color.White)
        .fillMaxWidth()) {

        val hour = remember { mutableStateOf(-1) }
        val minute = remember { mutableStateOf(-1) }
        val second = remember { mutableStateOf(-1) }

        tickerFlow(Duration.seconds(1), Duration.seconds(1))
            .map { Calendar.getInstance() }
            .distinctUntilChanged { old, new ->
                old.get(Calendar.SECOND) == new.get(Calendar.SECOND)
            }
            .onEach { calendar ->
                hour.value = calendar.get(Calendar.HOUR_OF_DAY)
                minute.value = calendar.get(Calendar.MINUTE)
                second.value = calendar.get(Calendar.SECOND)
            }
            .launchIn(CoroutineScope(Dispatchers.IO))

        BoxWithConstraints {
            val sixClockWidth = this.maxWidth / 2
            val sixClockHeight = this.maxHeight / 3
            Column(verticalArrangement = Arrangement.Top) {
                SixPartClockDisplayRow(
                    Modifier
                        .requiredWidthIn(0.dp, sixClockWidth)
                        .requiredHeightIn(0.dp, sixClockHeight),
                    digits = hour.value.twoRightMostDigits()
                )
                SixPartClockDisplayRow(
                    Modifier
                        .requiredWidthIn(0.dp, sixClockWidth)
                        .requiredHeightIn(0.dp, sixClockHeight),
                    digits = minute.value.twoRightMostDigits()
                )
                SixPartClockDisplayRow(
                    Modifier
                        .requiredWidthIn(0.dp, sixClockWidth)
                        .requiredHeightIn(0.dp, sixClockHeight),
                    digits = second.value.twoRightMostDigits()
                )
            }
        }
    }
}

private fun Int.twoRightMostDigits(): Pair<Int, Int> {
    return when (this) {
        -1 -> -1 to -1
        0 -> 0 to 0
        else -> {
            val firstDigit: Int = this % 10
            val secondDigit: Int = if (this < 10) 0 else this / 10 % 10
            firstDigit to secondDigit
        }
    }
}