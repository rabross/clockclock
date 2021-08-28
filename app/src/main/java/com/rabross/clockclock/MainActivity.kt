package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import com.rabross.clockclock.ui.PartClockGridDisplay
import com.rabross.clockclock.ui.randomAngle
import kotlin.math.atan2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //ClockClockTheme {
            val clocks = remember {
                mutableStateOf(
                    listOf(
                        randomAngle to randomAngle, randomAngle to randomAngle,
                        randomAngle to randomAngle, randomAngle to randomAngle,
                        randomAngle to randomAngle, randomAngle to randomAngle,
                        randomAngle to randomAngle, randomAngle to randomAngle,
                        randomAngle to randomAngle, randomAngle to randomAngle,
                        randomAngle to randomAngle, randomAngle to randomAngle,
                        randomAngle to randomAngle, randomAngle to randomAngle,
                        randomAngle to randomAngle,
                    )
                )
            }

            val offsetList = arrayOfNulls<Offset>(clocks.value.size)

            Surface(
                modifier = Modifier
                    .background(color = Color.White)
                    .fillMaxWidth()
                    .pointerInput(Unit) {

                        var offsetX = 0.0
                        var offsetY = 0.0

                        detectDragGestures({
                            offsetX = it.x.toDouble()
                            offsetY = it.y.toDouble()
                        }) { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            clocks.value = clocks.value.mapIndexed { index, _ ->
                                val deg = calcAngle(Offset(offsetX.toFloat(), offsetY.toFloat()), offsetList[index]!!)
                                deg to deg
                            }
                        }
                    }
            ) {

                PartClockGridDisplay(clocks.value, 3, 5) { index, offset ->
                    offsetList[index] = offset
                }
            }
        }
        //}
    }
    private fun calcAngle(origin: Offset, target: Offset): Float {
        return Math.toDegrees(atan2(target.y - origin.y, target.x - origin.x).toDouble()).toFloat() - 90
    }
}
