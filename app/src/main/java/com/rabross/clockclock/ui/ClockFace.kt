package com.rabross.clockclock.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ClockFace(
    modifier: Modifier = Modifier,
    hourHand: Int,
    minuteHand: Int
) {
    ClockFace(
        modifier = modifier,
        hourHandDegree = hourHand.toClockHourDegree(),
        minuteHandDegree = minuteHand.toClockMinuteDegree(),
    )
}

@Composable
fun ClockFace(
    modifier: Modifier = Modifier,
    hourHandDegree: Float = 225f,
    minuteHandDegree: Float = 225f
) {
    val handColor = Color(0xFF242424)
    val minuteIndicatorColor = Color(0xFFEEEEEE)
    val hourIndicatorColor = Color(0xFFDDDDDD)
    val shadowColor = Color(0x33000000)

    Spacer(
        modifier = modifier
            .aspectRatio(1f)
            .drawWithCache {
                val centerPoint = this.size.minDimension / 2.0f
                val borderWidth = centerPoint / 10
                val radius = centerPoint - borderWidth
                val handWidth = radius / 5
                val frameWidth = handWidth / 2
                val hourIndicatorLength = (radius - borderWidth) / 5
                val minuteIndicatorLength = hourIndicatorLength / 4
                val hourIndicatorHandWidth = radius * 0.02f
                val minuteIndicatorHandWidth = hourIndicatorHandWidth / 2
                val outlineWidth = minuteIndicatorHandWidth / 2
                val minuteHandLength = radius - frameWidth / 2 - outlineWidth
                val hourHandLength = minuteHandLength - hourIndicatorLength * 2f / 3f
                val indicatorOffset = borderWidth + frameWidth / 2 + hourIndicatorHandWidth
                val shadowDepth = borderWidth / 2

                onDrawBehind {
                    drawClockFace(radius)
                    drawMinuteIndicators(
                        minuteIndicatorColor,
                        minuteIndicatorLength,
                        minuteIndicatorHandWidth,
                        indicatorOffset
                    )
                    drawHourIndicators(
                        hourIndicatorColor,
                        hourIndicatorLength,
                        hourIndicatorHandWidth,
                        indicatorOffset
                    )
                    drawHand(hourHandDegree, hourHandLength, handColor, handWidth)
                    drawHand(minuteHandDegree, minuteHandLength, handColor, handWidth)
                    drawClockHandCenter(handColor, handWidth / 2)
                    drawClockShadow(shadowColor, radius, shadowDepth)
                    drawClockFrame(radius, frameWidth, outlineWidth)
                }
            }
    )
}

@Preview
@Composable
private fun ClockFaceFacePreview() {
    Surface {
        ClockFace(
            hourHandDegree = randomAngle,
            minuteHandDegree = randomAngle
        )
    }
}
