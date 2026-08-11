package com.rabross.clockclock.ui

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DebossedClockFace(
    modifier: Modifier = Modifier,
    hourHandDegree: Float,
    minuteHandDegree: Float,
    handColor: Color = Color.DarkGray
) {
    DebossedCircle(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        val radius = size.minDimension / 2f
                        val handWidth = radius / 5.6f
                        val handLength = radius * 0.98f
                        drawHand(hourHandDegree, handLength, handColor, handWidth)
                        drawHand(minuteHandDegree, handLength * 0.87f, handColor, handWidth)
                        drawClockHandCenter(handColor, handWidth / 2)
                    }
                }
        )
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
            .innerShadow(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.6f),
                blur = 8.dp,
                offsetX = (-4).dp,
                offsetY = (-4).dp
            )
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

        canvas.drawOutline(shadowOutline, paint)

        paint.asFrameworkPaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            if (blur.toPx() > 0) {
                maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
        }

        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)

        paint.asFrameworkPaint().xfermode = null
        paint.asFrameworkPaint().maskFilter = null
        canvas.restore()
    }
}

@Preview
@Composable
fun DebossedClockFacePreview() {
    Surface {
        Box(
            modifier = Modifier
                .background(color = Color(0xFFE0E0E0))
                .size(200.dp)
        ) {
            DebossedClockFace(
                modifier = Modifier.fillMaxSize(),
                hourHandDegree = randomAngle,
                minuteHandDegree = randomAngle
            )
        }
    }
}
