package com.rabross.clockclock.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

typealias PartClock = Pair<Float, Float>
typealias SixPartClock = Array<PartClock>

sealed class Number(val partClocks: SixPartClock) {

    companion object {
        private val BLANK = 225f to 225f

        fun map(number: Int): Number {
            return when(number){
                1 -> One
                2 -> Two
                3 -> Three
                4 -> Four
                5 -> Five
                6 -> Six
                7 -> Seven
                8 -> Eight
                9 -> Nine
                0 -> Zero
                else -> Blank
            }
        }
    }

    object Blank : Number(arrayOf(
        BLANK, BLANK,
        BLANK, BLANK,
        BLANK, BLANK))
    object Zero : Number(arrayOf(
        90f to 180f, 270f to 180f,
        0f to 180f, 180f to 0f,
        0f to 90f, 0f to 270f))
    object One : Number(arrayOf(
        BLANK, 180f to 180f,
        BLANK, 0f to 180f,
        BLANK, 0f to 0f))
    object Two : Number(arrayOf(
        90f to 90f, 270f to 180f,
        180f to 90f, 0f to 270f,
        0f to 90f, 270f to 270f))
    object Three : Number(arrayOf(
        90f to 90f, 270f to 180f,
        90f to 90f, 0f to 270f,
        90f to 90f, 0f to 270f))
    object Four : Number(arrayOf(
        180f to 180f, 180f to 180f,
        0f to 90f, 0f to 180f,
        BLANK, 0f to 0f))
    object Five : Number(arrayOf(
        90f to 180f, 270f to 270f,
        0f to 90f, 180f to 270f,
        90f to 90f, 0f to 270f))
    object Six : Number(arrayOf(
        180f to 90f, 270f to 270f,
        0f to 180f, 270f to 180f,
        0f to 90f, 0f to 270f))
    object Seven : Number(arrayOf(
        90f to 90f, 270f to 180f,
        BLANK, 180f to 0f,
        BLANK, 0f to 0f))
    object Eight : Number(arrayOf(
        90f to 180f, 180f to 270f,
        0f to 90f, 270f to 0f,
        0f to 90f, 270f to 0f))
    object Nine : Number(arrayOf(
        90f to 180f, 270f to 180f,
        0f to 90f, 0f to 180f,
        90f to 90f, 270f to 0f))
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
fun PartClock(
    hourHand: Int,
    minuteHand: Int,
    modifier: Modifier = Modifier,
    duration: Int = 500
) {
    PartClock(
        hourHand = hourHand.toClockHourDegree().toFloat(),
        minuteHand = minuteHand.toClockMinuteDegree().toFloat(),
        modifier,
        duration
    )
}

@Composable
fun PartClock(
    hourHand: Float,
    minuteHand: Float,
    modifier: Modifier = Modifier,
    duration: Int = 800,
) {
    val hourDegree by animateFloatAsState(
        hourHand,
        tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )
    val minuteDegree by animateFloatAsState(
        minuteHand,
        tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )

    Canvas(modifier = modifier.aspectRatio(1f), onDraw = {
        val handColor = Color(0xFF222222)
        val minuteIndicatorColor = Color(0xFFEEEEEE)
        val hourIndicatorColor = Color(0xFFDDDDDD)
        val shadowColor = Color(0x33000000)
        val outlineWidth = 1.dp.toPx()
        val center = size.minDimension / 2.0f
        val borderWidth = center / 10
        val radius = center - borderWidth
        val handWidth= radius / 5
        val frameWidth = handWidth / 2
        val hourIndicatorLength = (radius - borderWidth)/5
        val minuteIndicatorLength = hourIndicatorLength/4
        val indicatorOffset = borderWidth + frameWidth/2 + 2.dp.toPx()
        val minuteHandLength = radius - frameWidth / 2 - outlineWidth
        val hourHandLength = minuteHandLength - hourIndicatorLength * 2/3
        val shadowDepth = borderWidth / 2

        drawClockFace(radius)
        drawMinuteIndicators(minuteIndicatorColor, minuteIndicatorLength, 2.dp.toPx(), indicatorOffset)
        drawHourIndicators(hourIndicatorColor, hourIndicatorLength,  2.dp.toPx(), indicatorOffset)
        drawHand(hourDegree, hourHandLength, handColor, handWidth)
        drawHand(minuteDegree, minuteHandLength, handColor, handWidth)
        drawClockHandCenter(handColor, handWidth / 2)
        drawClockShadow(shadowColor, radius, shadowDepth)
        drawClockFrame(radius, frameWidth, outlineWidth)
    })
}

private fun DrawScope.drawClockHandCenter(color: Color, radius: Float){
    drawCircle(color, radius)
    drawCircle(color = Color.Black, radius = radius/3, style = Stroke(4f))
}

private fun DrawScope.drawClockShadow(color: Color, radius: Float, depth: Float){
    drawCircle(color, radius = radius, center = center.copy(y = center.y + depth),style = Stroke(depth * 2))
}

private fun DrawScope.drawClockFace(radius: Float){
    drawCircle(Color.White, radius = radius)
}

private fun DrawScope.drawClockFrame(radius: Float, frameWidth: Float, outlineWidth: Float){
    drawCircle(Color.White, radius = radius, style = Stroke(frameWidth))
    drawCircle(Color.LightGray, radius = radius - frameWidth/2, style = Stroke(outlineWidth))
    drawCircle(Color.LightGray, radius = radius + frameWidth/2, style = Stroke(outlineWidth))
}

private fun DrawScope.drawHourIndicators(color: Color, length: Float, width: Float, offset: Float){
    for (i in 0..330 step 30) {
        drawIndicator(i.toFloat(), length, color, width, offset)
    }
}

private fun DrawScope.drawMinuteIndicators(color: Color, length: Float, width: Float, offset: Float){
    for (i in 0..354 step 6) {
        drawIndicator(i.toFloat(), length, color, width, offset)
    }
}

private fun DrawScope.drawIndicator(
    degree: Float,
    length: Float,
    colour: Color,
    width: Float,
    offset: Float
) {
    drawContext.canvas.withSave {
        drawContext.transform.rotate(degree)
        drawLine(
            colour,
            center.copy(y = offset),
            center.copy(y = offset + length),
            strokeWidth = width
        )
    }
}

private fun DrawScope.drawHand(
    degree: Float,
    length: Float,
    handColor: Color,
    handWidth: Float,
) {
    drawContext.canvas.withSave {
        drawContext.transform.rotate(degree)
        drawLine(
            handColor,
            center,
            center.copy(y = center.y - length),
            strokeWidth = handWidth
        )
    }
}

private fun Int.toClockHourDegree() = this * 360 / 12

//private fun Int.toClockDegree() = this % 12 * 360 / 12
private fun Int.toClockMinuteDegree() = this * 360 / 60

private val randomAngle
    get() = Math.random().toFloat() * 360f

private inline fun <reified T> Pair<T,T>.reverseMap(block: (value: T) -> Unit){
    block(second)
    block(first)
}

@Preview
@Composable
private fun PartClockPreview() {
    Surface(modifier = Modifier.background(color = Color.White)) {
        PartClock(hourHand = randomAngle,
            minuteHand = randomAngle,
            Modifier, 0)
    }
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