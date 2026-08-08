package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.rabross.clockclock.ui.ClockViewModel
import com.rabross.clockclock.ui.PartClockGridDisplay
import com.rabross.clockclock.ui.theme.ClockClockTheme
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {

    private val viewModel: ClockViewModel by viewModels()

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClockClockTheme {
                ClockClockScreen(viewModel)
            }
        }
    }
}

@Composable
fun ClockClockScreen(viewModel: ClockViewModel) {
    val clocks by viewModel.clocks
    val isDragging by viewModel.isDragging

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
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
                shouldAnimate = !isDragging,
                modifier = Modifier.fillMaxSize()
            ) { index, offset ->
                viewModel.reportPosition(index, offset)
            }
        }
    }
}
