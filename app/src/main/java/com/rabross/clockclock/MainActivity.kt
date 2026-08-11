package com.rabross.clockclock

import android.content.res.Configuration
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.rabross.clockclock.ui.ClockViewModel
import com.rabross.clockclock.ui.DebossedClockFace
import com.rabross.clockclock.ui.ClocksGridDisplay
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

    override fun onPause() {
        viewModel.screenPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.screenResume()
    }
}

@Composable
fun ClockClockScreen(viewModel: ClockViewModel) {
    val clocks by viewModel.clocks
    val isDragging by viewModel.isDragging
    val columns by viewModel.columns
    val rows by viewModel.rows

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(isLandscape) {
        if (isLandscape) {
            viewModel.updateDimensions(12, 3)
        } else {
            viewModel.updateDimensions(4, 9)
        }
    }

    Surface(
        color =  Color(0xFFE0E0E0),//MaterialTheme.colorScheme.background,
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
            ClocksGridDisplay(
                clocks = clocks,
                countX = columns,
                countY = rows,
                shouldAnimate = !isDragging,
                modifier = Modifier.fillMaxSize(),
                reportPosition = { index, offset ->
                    viewModel.reportPosition(index, offset)
                }
            ) { hour, minute, modifier ->
                DebossedClockFace(
                    modifier = modifier.padding(2.dp),
                    hourHandDegree = hour,
                    minuteHandDegree = minute
                )
            }
        }
    }
}
