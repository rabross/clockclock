package com.rabross.clockclock.ui

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun PartClockPreview() {
    Surface {
        PartClock(
            hourHandDegree = randomAngle,
            minuteHandDegree = randomAngle
        )
    }
}

@Composable
fun PartClockGridDisplay(
    partClocks: List<Pair<Float, Float>>,
    countX: Int,
    countY: Int,
    modifier: Modifier = Modifier,
    shouldAnimate: Boolean = true,
    reportPosition: (Int, Offset) -> Unit = { _, _ -> }
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
                        val partClock = partClocks[index]
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

                        PartClock(
                            modifier = Modifier
                                .size(clockSize)
                                .onGloballyPositioned { coordinates ->
                                    reportPosition(index, coordinates.boundsInRoot().center)
                                },
                            hourHandDegree = hourDegree.value,
                            minuteHandDegree = minuteDegree.value
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
        modifier,
        hourHand.toClockHourDegree(),
        minuteHand.toClockMinuteDegree(),
    )
}

@Composable
fun PartClock(
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
                    drawMinuteIndicators(minuteIndicatorColor, minuteIndicatorLength, minuteIndicatorHandWidth, indicatorOffset)
                    drawHourIndicators(hourIndicatorColor, hourIndicatorLength, hourIndicatorHandWidth, indicatorOffset)
                    drawHand(hourHandDegree, hourHandLength, handColor, handWidth)
                    drawHand(minuteHandDegree, minuteHandLength, handColor, handWidth)
                    drawClockHandCenter(handColor, handWidth / 2)
                    drawClockShadow(shadowColor, radius, shadowDepth)
                    drawClockFrame(radius, frameWidth, outlineWidth)
                }
            }
    )
}

private fun DrawScope.drawClockHandCenter(color: Color, radius: Float) {
    drawCircle(color, radius)
    drawCircle(color = color, radius = radius / 3, style = Stroke(4f))
}

private fun DrawScope.drawClockShadow(color: Color, radius: Float, depth: Float) {
    drawCircle(color, radius = radius, center = center.copy(y = center.y + depth), style = Stroke(depth * 2))
}

private fun DrawScope.drawClockFace(radius: Float) {
    drawCircle(Color.White, radius = radius)
}

private fun DrawScope.drawClockFrame(radius: Float, frameWidth: Float, outlineWidth: Float) {
    drawCircle(Color.White, radius = radius, style = Stroke(frameWidth))
    drawCircle(Color.LightGray, radius = radius - frameWidth / 2, style = Stroke(outlineWidth))
    drawCircle(Color.LightGray, radius = radius + frameWidth / 2, style = Stroke(outlineWidth))
}

private fun DrawScope.drawHourIndicators(color: Color, length: Float, width: Float, offset: Float) {
    for (i in 0..330 step 30) {
        drawIndicator(i.toFloat(), length, color, width, offset)
    }
}

private fun DrawScope.drawMinuteIndicators(color: Color, length: Float, width: Float, offset: Float) {
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

@Preview
@Composable
fun DebossedClock() {
    Surface {
        Box(
            modifier = Modifier
                .background(color = Color(0xFFE0E0E0))
                .size(200.dp)
        ) {
            DebossedCircle {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithCache {
                            onDrawBehind {
                                val radius = size.minDimension / 2f
                                val handWidth = radius / 5.6f
                                val handLength = radius * 0.98f

                                val hourHandDegree: Float = randomAngle
                                val minuteHandDegree: Float = randomAngle
                                drawHand(hourHandDegree, handLength, Color.DarkGray, handWidth)
                                drawHand(minuteHandDegree, handLength * 0.87f, Color.DarkGray, handWidth)
                                drawClockHandCenter(Color.DarkGray, handWidth/2)
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun DebossedCircle(
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFE0E0E0), // Matches background
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            // The light highlight (bottom-right)
            .innerShadow(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.6f),
                blur = 8.dp,
                offsetX = (-4).dp,
                offsetY = (-4).dp
            )
            // The dark shadow (top-left)
            .innerShadow(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                blur = 8.dp,
                offsetX = 4.dp,
                offsetY = 4.dp
            )
            .background(containerColor, CircleShape),
        content = content
    )
}

fun Modifier.innerShadow(
    shape: Shape,
    color: Color = Color.Black.copy(alpha = 0.25f),
    blur: Dp = 4.dp,
    offsetX: Dp = 2.dp,
    offsetY: Dp = 2.dp
) = drawWithContent {
    drawContent()

    val rect = Rect(Offset.Zero, size)
    val paint = Paint().apply {
        this.color = color
        this.isAntiAlias = true
    }

    val shadowOutline = shape.createOutline(size, layoutDirection, this)

    drawIntoCanvas { canvas ->
        canvas.saveLayer(rect, paint)

        // Draw the base shadow color inside the shape
        canvas.drawOutline(shadowOutline, paint)

        // Configure paint to "cut out" the offset shape
        paint.asFrameworkPaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            if (blur.toPx() > 0) {
                maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
        }

        // Offset and draw the cutout
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)

        paint.asFrameworkPaint().xfermode = null
        paint.asFrameworkPaint().maskFilter = null
        canvas.restore()
    }
}