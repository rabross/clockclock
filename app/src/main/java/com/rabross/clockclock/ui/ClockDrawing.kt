package com.rabross.clockclock.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.withSave

fun DrawScope.drawClockHandCenter(color: Color, radius: Float) {
    drawCircle(color, radius)
    drawCircle(color = color, radius = radius / 3, style = Stroke(4f))
}

fun DrawScope.drawClockShadow(color: Color, radius: Float, depth: Float) {
    drawCircle(color, radius = radius, center = center.copy(y = center.y + depth), style = Stroke(depth * 2))
}

fun DrawScope.drawClockFace(radius: Float) {
    drawCircle(Color.White, radius = radius)
}

fun DrawScope.drawClockFrame(radius: Float, frameWidth: Float, outlineWidth: Float) {
    drawCircle(Color.White, radius = radius, style = Stroke(frameWidth))
    drawCircle(Color.LightGray, radius = radius - frameWidth / 2, style = Stroke(outlineWidth))
    drawCircle(Color.LightGray, radius = radius + frameWidth / 2, style = Stroke(outlineWidth))
}

fun DrawScope.drawHourIndicators(color: Color, length: Float, width: Float, offset: Float) {
    for (i in 0..330 step 30) {
        drawIndicator(i.toFloat(), length, color, width, offset)
    }
}

fun DrawScope.drawMinuteIndicators(color: Color, length: Float, width: Float, offset: Float) {
    for (i in 0..354 step 6) {
        drawIndicator(i.toFloat(), length, color, width, offset)
    }
}

fun DrawScope.drawIndicator(
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

fun DrawScope.drawHand(
    degree: Float,
    length: Float,
    brush: Brush,
    handWidth: Float,
) {
    drawContext.canvas.withSave {
        drawContext.transform.rotate(degree)
        drawLine(
            brush = brush,
            start = center,
            end = center.copy(y = center.y - length),
            strokeWidth = handWidth,
            cap = StrokeCap.Butt
        )
    }
}

fun DrawScope.drawHand(
    degree: Float,
    length: Float,
    handColor: Color,
    handWidth: Float,
) {
    drawHand(degree, length, SolidColor(handColor), handWidth)
}

fun Int.toClockHourDegree() = this * 360f / 12

fun Int.toClockMinuteDegree() = this * 360f / 60
