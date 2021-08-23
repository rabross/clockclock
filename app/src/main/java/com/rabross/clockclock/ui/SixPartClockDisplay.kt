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
}

@Preview
@Composable
private fun SixPartClockDisplayPreview() {
    Surface(modifier = Modifier.background(color = Color.White)) {
        BoxWithConstraints {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
//                SixPartClockDisplay(Number.One, Modifier.weight(1.0f))
                SixPartClockDisplay(Number.Three, Modifier.weight(1.0f))
            }
        }
    }
}

@Composable
fun SixPartClockDisplay(number: Number, modifier: Modifier = Modifier) {
    SixPartClockDisplay(number.partClocks, modifier)
}

@Composable
fun SixPartClockDisplay(partClocks: SixPartClock, modifier: Modifier = Modifier) {
    val rowSize = 2
    val columnSize = 3
    BoxWithConstraints(modifier) {
        val clockSize = this.maxWidth / rowSize
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            (0 until columnSize).forEach { row ->
                Row(Modifier) {
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
fun PartClock(
    hourHand: Int,
    minuteHand: Int,
    duration: Int = 500,
    modifier: Modifier = Modifier,
) {
    PartClock(
        hourHand = hourHand.toClockHourDegree().toFloat(),
        minuteHand = minuteHand.toClockMinuteDegree().toFloat(),
        duration,
        modifier
    )
}

@Composable
fun PartClock(
    hourHand: Float,
    minuteHand: Float,
    duration: Int = 500,
    modifier: Modifier = Modifier,
) {
    val hourDegree by animateFloatAsState(
        hourHand,
        tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )
    val minuteDegree by animateFloatAsState(
        minuteHand,
        tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )

    Canvas(modifier = modifier.fillMaxSize(), onDraw = {
        val handColor = Color(0xFF222222)
        val handWidth = 16.dp.toPx()
        val borderWidth = 12.dp.toPx()
        val frameWidth = 8.dp.toPx()
        val outlineWidth = 1.dp.toPx()
        val depth = 4.dp.toPx()
        val radius = size.minDimension / 2.0f - borderWidth
        val hourIndicatorLength = (radius - borderWidth)/5
        val minuteIndicatorLength = hourIndicatorLength/4
        val indicatorOffset = borderWidth + frameWidth/2 + 2.dp.toPx()
        val minuteHandLength = radius - frameWidth / 2 - outlineWidth
        val hourHandLength = minuteHandLength - hourIndicatorLength * 2/3

        drawClockShadow(radius, depth)
        drawClockFrame(radius, frameWidth, outlineWidth)
        drawMinuteIndicators(Color.Gray, minuteIndicatorLength, 2.dp.toPx(), indicatorOffset)
        drawHourIndicators(Color.DarkGray, hourIndicatorLength,  2.dp.toPx(), indicatorOffset)
        drawHand(hourDegree, hourHandLength, handColor, handWidth)
        drawHand(minuteDegree, minuteHandLength, handColor, handWidth)
        drawClockHandCenter(handColor, handWidth / 2)
    })
}

private fun DrawScope.drawClockHandCenter(color: Color, radius: Float){
    drawCircle(color, radius)
    drawCircle(color = Color.Black, radius = radius/3, style = Stroke(4f))
}

private fun DrawScope.drawClockShadow(radius: Float, depth: Float){
    drawCircle(Color.LightGray, radius = radius, center = center.copy(y = center.y + depth),style = Stroke(depth * 2))
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