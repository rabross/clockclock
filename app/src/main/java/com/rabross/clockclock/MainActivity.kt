package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.rabross.clockclock.ui.ClockViewModel
import com.rabross.clockclock.ui.PartClockGridDisplay
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {

    private val viewModel: ClockViewModel by viewModels()

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockClockScreen(viewModel)
        }
    }
}

@Composable
fun ClockClockScreen(viewModel: ClockViewModel) {
    val clocks by viewModel.clocks
    val isDragging by viewModel.isDragging

    Surface(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .pointerInput(Unit) {
                var offsetX = -1f
                var offsetY = -1f

                detectDragGestures(
                    onDragStart = {
                        viewModel.onDragStart()
                        offsetX = it.x
                        offsetY = it.y
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        viewModel.onDrag(offsetX, offsetY)
                    },
                    onDragEnd = {
                        viewModel.onDragEnd()
                    }
                )
            }
    ) {
        PartClockGridDisplay(
            partClocks = clocks,
            countX = 4,
            countY = 9,
            shouldAnimate = !isDragging
        ) { index, offset ->
            viewModel.reportPosition(index, offset)
        }
    }
}
