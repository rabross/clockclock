package com.rabross.clockclock.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GoldClockFace(
    modifier: Modifier = Modifier,
    hourHandDegree: Float,
    minuteHandDegree: Float,
    containerColor: Color = Color(0xFF1A1A1A)
) {
    DebossedCircle(
        modifier = modifier,
        containerColor = containerColor
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val goldBrush = metallicGoldBrush(size)
                    onDrawBehind {
                        val radius = size.minDimension / 2f
                        val handWidth = radius / 5.6f
                        val handLength = radius * 0.98f
                        drawHand(hourHandDegree, handLength, goldBrush, handWidth)
                        drawHand(minuteHandDegree, handLength * 0.87f, goldBrush, handWidth)
                        drawCircle(brush = goldBrush, radius = handWidth / 2f)
                    }
                }
        )
    }
}

@Preview
@Composable
fun GoldClockFacePreview() {
    Surface {
        GoldClockFace(
            modifier = Modifier.size(200.dp),
            hourHandDegree = 45f,
            minuteHandDegree = 180f
        )
    }
}

val GoldShadow = Color(0xFF755E19)
val GoldBase = Color(0xFFD4AF37)
val GoldHighlight = Color(0xFFF7EF8A)

fun metallicGoldBrush(size: Size) = Brush.linearGradient(
    colors = listOf(
        GoldShadow,
        GoldBase,
        GoldHighlight,
        Color.White,
        GoldHighlight,
        GoldBase,
        GoldShadow
    ),
    start = Offset.Zero,
    end = Offset(size.width, size.height)
)
