package com.rabross.clockclock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rabross.clockclock.ui.models.Number
import com.rabross.clockclock.ui.models.SixPartClock

@Composable
fun SixPartClockDisplay(
    number: Number,
    modifier: Modifier = Modifier,
    duration: Int = 500
) {
    SixPartClockDisplay(
        number.partClocks,
        modifier,
        duration
    )
}

@Composable
fun SixPartClockDisplay(
    partClocks: SixPartClock,
    modifier: Modifier = Modifier,
    duration: Int = 500
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
                            modifier = Modifier.size(clockSize),
                            duration
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
                modifier,
                500
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
            Modifier,
            0
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
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                    SixPartClockDisplay(
                        Number.Two,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                    SixPartClockDisplay(
                        Number.Three,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.Four,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                    SixPartClockDisplay(
                        Number.Five,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                    SixPartClockDisplay(
                        Number.Six,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.Seven,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                    SixPartClockDisplay(
                        Number.Eight,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                    SixPartClockDisplay(
                        Number.Nine,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.Blank,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                    SixPartClockDisplay(
                        Number.Zero,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                    SixPartClockDisplay(
                        Number.Blank,
                        Modifier
                            .requiredWidthIn(0.dp, clockWidth)
                            .requiredHeightIn(0.dp, clockHeight),
                        0
                    )
                }
            }
        }
    }
}