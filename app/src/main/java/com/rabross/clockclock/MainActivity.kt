package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import com.rabross.clockclock.ui.PartClockGridDisplay
import com.rabross.clockclock.ui.tickerFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.atan2
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private const val AT_REST = 225f

class MainActivity : ComponentActivity() {

    private val rows = 10
    private val columns = 6
    private val clockCount = rows * columns
    private val defaultOffset = Offset(0f,0f)
    private val offsetMap = mutableMapOf<Int, Offset>()

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //ClockClockTheme {

            val clocks = mutableListOf<Pair<MutableState<Float>, MutableState<Float>>>().apply {
                repeat(clockCount) {
                    add(remember { mutableStateOf(AT_REST) } to remember { mutableStateOf(AT_REST) })
                }
            }

            var offsetX by remember { mutableStateOf(AT_REST) }
            var offsetY by remember { mutableStateOf(AT_REST) }

            Surface(
                modifier = Modifier
                    .background(color = Color.White)
                    .fillMaxWidth()
                    .pointerInput(Unit) {

                        /*detectTapGestures {
                            offsetX = it.x
                            offsetY = it.y
                        }*/

                        detectDragGestures(
                            onDragStart = {
                                idleAnimtion?.cancel()
                                offsetX = it.x
                                offsetY = it.y
                            },
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            },
                            onDragEnd = {
                                val anim = animations.random()
                                idleAnimtion = startTicker(Duration.seconds(2)) {
                                    clocks.forEach(anim)
                                }
                            }
                        )
                    }
            ) {

                clocks.forEachIndexed { index, item ->
                    val origin = Offset(offsetX, offsetY)
                    val target = offsetMap.getOrElse(index) { Offset(offsetX+1, offsetY-1) }
                    val deg = calcAngle(origin, target) % 360

                    item.first.value = deg
                    item.second.value = deg
                }

                //Log.i("rob", "clock target ${clocks[0].first.value}")

                PartClockGridDisplay(clocks, columns, rows) { index, offset ->
                    offsetMap[index] = offset
                }
            }
        }
        //}
    }

    private fun calcAngle(origin: Offset, target: Offset): Float {
        val degrees = Math.toDegrees(atan2(target.y - origin.y, target.x - origin.x).toDouble()).toFloat()
        val clockDegrees = degrees - 90
        val normalise = if(clockDegrees < 0) clockDegrees + 360 else clockDegrees
        return normalise
    }

    private val animDiff: (Pair<MutableState<Float>, MutableState<Float>>) -> Unit =
        { item ->
            item.first.value = item.first.value + 0.05f
            item.second.value = item.second.value + 0.025f
        }

    private val animSame: (Pair<MutableState<Float>, MutableState<Float>>) -> Unit =
        { item ->
            item.first.value = item.first.value + 0.05f
            item.second.value = item.second.value + 0.05f
        }

    private val animOpposite: (Pair<MutableState<Float>, MutableState<Float>>) -> Unit =
        { item ->
            item.first.value = item.first.value - 0.05f
            item.second.value = item.second.value + 0.05f
        }

    private var idleAnimtion: Job? = null
    private val animations = listOf(animDiff, animOpposite, animSame)

    @ExperimentalTime
    private fun startTicker(startDelay: Duration = Duration.seconds(1), onTick: () -> Unit): Job {
        return tickerFlow(Duration.milliseconds(1), startDelay)
            .onEach { onTick() }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }
}
