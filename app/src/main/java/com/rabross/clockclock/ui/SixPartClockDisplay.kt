package com.rabross.clockclock.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

typealias PartClock = Pair<Float, Float>
typealias SixPartClock = Array<PartClock>

sealed class Number(val partClocks: SixPartClock) {

    companion object {
        private val BLANK = 225f to 225f
    }

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

@Preview
@Composable
private fun SixPartClockDisplayPreview() {
    Surface(modifier = Modifier.background(color = Color.White)) {
        BoxWithConstraints {
            val clockWidth = this.maxWidth / 3
            val clockHeight = this.maxHeight / 3
            Column(verticalArrangement = Arrangement.Top) {
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.One,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                    SixPartClockDisplay(
                        Number.Two,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                    SixPartClockDisplay(
                        Number.Three,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.Four,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                    SixPartClockDisplay(
                        Number.Five,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                    SixPartClockDisplay(
                        Number.Six,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                }
                Row(horizontalArrangement = Arrangement.Start) {
                    SixPartClockDisplay(
                        Number.Seven,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                    SixPartClockDisplay(
                        Number.Eight,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                    SixPartClockDisplay(
                        Number.Nine,
                        Modifier
                            .width(clockWidth)
                            .height(clockHeight),
                        500,
                        8.dp,
                        4.dp,
                        4.dp
                    )
                }
            }
        }
    }
}

@Composable
fun SixPartClockDisplay(
    number: Number, modifier: Modifier = Modifier,
    duration: Int = 500,
    handWidth: Dp = 16.dp,
    frameWidth: Dp = 8.dp,
    borderWidth: Dp = 8.dp
) {
    SixPartClockDisplay(
        number.partClocks, modifier,
        duration,
        handWidth,
        frameWidth,
        borderWidth
    )
}

@Composable
fun SixPartClockDisplay(
    partClocks: SixPartClock, modifier: Modifier = Modifier,
    duration: Int = 500,
    handWidth: Dp = 16.dp,
    frameWidth: Dp = 8.dp,
    borderWidth: Dp = 8.dp
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
                            duration,
                            handWidth,
                            frameWidth,
                            borderWidth
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
    duration: Int = 500,
    handWidth: Dp = 16.dp,
    frameWidth: Dp = 8.dp,
    borderWidth: Dp = 8.dp
) {
    PartClock(
        hourHand = hourHand.toClockHourDegree().toFloat(),
        minuteHand = minuteHand.toClockMinuteDegree().toFloat(),
        modifier,
        duration,
        handWidth,
        frameWidth,
        borderWidth
    )
}

@Composable
fun PartClock(
    hourHand: Float,
    minuteHand: Float,
    modifier: Modifier = Modifier,
    duration: Int = 800,
    handWidth: Dp = 16.dp,
    frameWidth: Dp = 8.dp,
    borderWidth: Dp = 8.dp
) {
    val hourDegree by animateFloatAsState(
        hourHand,
        tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )
    val minuteDegree by animateFloatAsState(
        minuteHand,
        tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )

    Canvas(modifier = modifier.aspectRatio(1f, false), onDraw = {
        val handColor = Color(0xFF222222)
        val minuteIndicatorColor = Color(0xFFEEEEEE)
        val hourIndicatorColor = Color(0xFFDDDDDD)
        val shadowColor = Color(0x33000000)
        val outlineWidth = 1.dp.toPx()
        val radius = size.minDimension / 2.0f - borderWidth.toPx()
        val hourIndicatorLength = (radius - borderWidth.toPx())/5
        val minuteIndicatorLength = hourIndicatorLength/4
        val indicatorOffset = borderWidth.toPx() + frameWidth.toPx()/2 + 2.dp.toPx()
        val minuteHandLength = radius - frameWidth.toPx() / 2 - outlineWidth
        val hourHandLength = minuteHandLength - hourIndicatorLength * 2/3
        val shadowDepth = borderWidth.toPx() / 2

        drawMinuteIndicators(minuteIndicatorColor, minuteIndicatorLength, 2.dp.toPx(), indicatorOffset)
        drawHourIndicators(hourIndicatorColor, hourIndicatorLength,  2.dp.toPx(), indicatorOffset)
        drawClockShadow(shadowColor, radius, shadowDepth)
        drawClockFrame(radius, frameWidth.toPx(), outlineWidth)
        drawHand(hourDegree, hourHandLength, handColor, handWidth.toPx())
        drawHand(minuteDegree, minuteHandLength, handColor, handWidth.toPx())
        drawClockHandCenter(handColor, handWidth.toPx() / 2)
    })
}

private fun DrawScope.drawClockHandCenter(color: Color, radius: Float){
    drawCircle(color, radius)
    drawCircle(color = Color.Black, radius = radius/3, style = Stroke(4f))
}

private fun DrawScope.drawClockShadow(color: Color, radius: Float, depth: Float){
    drawCircle(color, radius = radius, center = center.copy(y = center.y + depth),style = Stroke(depth * 2))
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

@Composable
private fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        toPx()
    }
}

@Preview
@Composable
private fun PartClockPreview() {
    Surface(modifier = Modifier.background(color = Color.White)) {
        PartClock(hourHand = 9, minuteHand = 0)
    }
}

@Preview
@Composable
private fun PartClockAnimationPreview() {
    Surface(modifier = Modifier.background(color = Color.White)) {
        val hour = remember { mutableStateOf(randomAngle) }
        val minute = remember { mutableStateOf(randomAngle) }
        LaunchedEffect(key1 = Unit, block = {
            while (true) {
                delay(1000)
                hour.value = randomAngle
                minute.value = randomAngle
            }
        })
        PartClock(hourHand = hour.value, minuteHand = minute.value, duration = 500)
    }
}

private val randomAngle
    get() = Math.random().toFloat() * 360f