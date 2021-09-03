package com.rabross.clockclock.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun PartClockPreview() {
    Surface(modifier = Modifier.background(color = Color.White)) {
        PartClock(randomAngle, randomAngle, Modifier)
    }
}

/*@Preview
@Composable
private fun PartClockGridDisplayPreview() {
    val clocks = listOf(
        randomAngle to randomAngle, randomAngle to randomAngle,
        randomAngle to randomAngle, randomAngle to randomAngle,
        randomAngle to randomAngle, randomAngle to randomAngle,
        randomAngle to randomAngle, randomAngle to randomAngle
    )

    Surface(modifier = Modifier.background(color = Color.White)) {
        PartClockGridDisplay(clocks, 2, 4)
    }
}*/

@Composable
fun PartClockGridDisplay(
    partClocks: List<Pair<Float, Float>>,
    countX: Int,
    countY: Int,
    modifier: Modifier = Modifier,
    reportPosition: (Int, Offset) -> Unit = { _, _ -> }
) {
    BoxWithConstraints(modifier) {
        val clockWidth = this.maxWidth / countX
        val clockHeight = this.maxHeight / countY
        val clockSize = clockWidth.coerceAtMost(clockHeight)
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            (0 until countY).forEach { row ->
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    (0 until countX).forEach { column ->
                        val index = (row * countX) + column
                        val partClock = partClocks[index]
                        PartClock(partClock.first, partClock.second,
                            modifier = Modifier
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

@Composable
fun PartClock(
    hourHand: Int,
    minuteHand: Int,
    modifier: Modifier = Modifier
) {
    PartClock(
        hourHand.toClockHourDegree(),
        minuteHand.toClockMinuteDegree(),
        modifier
    )
}

@Composable
fun PartClock(
    hourHandDegree: Float = 225f,
    minuteHandDegree: Float = 225f,
    modifier: Modifier = Modifier
) {
    /*val hourDegree by animateFloatAsState(
        hourHand,
        tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )
    val minuteDegree by animateFloatAsState(
        minuteHand,
        tween(durationMillis = duration, easing = FastOutSlowInEasing)
    )*/

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
    ) {
        val handColor = Color(0xFF242424)
        val minuteIndicatorColor = Color(0xFFEEEEEE)
        val hourIndicatorColor = Color(0xFFDDDDDD)
        val shadowColor = Color(0x33000000)
        val center = size.minDimension / 2.0f
        val borderWidth = center / 10
        val radius = center - borderWidth
        val handWidth = radius / 5
        val frameWidth = handWidth / 2
        val hourIndicatorLength = (radius - borderWidth)/5
        val minuteIndicatorLength = hourIndicatorLength/4
        val hourIndicatorHandWidth = radius * 0.02f
        val minuteIndicatorHandWidth = hourIndicatorHandWidth / 2
        val outlineWidth = minuteIndicatorHandWidth / 2
        val minuteHandLength = radius - frameWidth / 2 - outlineWidth
        val hourHandLength = minuteHandLength - hourIndicatorLength * 2/3
        val indicatorOffset = borderWidth + frameWidth/2 + hourIndicatorHandWidth
        val shadowDepth = borderWidth / 2

        drawClockFace(radius)
        drawMinuteIndicators(minuteIndicatorColor, minuteIndicatorLength, minuteIndicatorHandWidth, indicatorOffset)
        drawHourIndicators(hourIndicatorColor, hourIndicatorLength,  hourIndicatorHandWidth, indicatorOffset)
        drawHand(hourHandDegree, hourHandLength, handColor, handWidth)
        drawHand(minuteHandDegree, minuteHandLength, handColor, handWidth)
        drawClockHandCenter(handColor, handWidth / 2)
        drawClockShadow(shadowColor, radius, shadowDepth)
        drawClockFrame(radius, frameWidth, outlineWidth)
    }
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

private fun Int.toClockHourDegree() = this * 360f / 12

private fun Int.toClockMinuteDegree() = this * 360f / 60