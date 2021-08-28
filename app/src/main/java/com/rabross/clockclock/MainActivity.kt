package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
                    add(remember { mutableStateOf(225f) } to remember { mutableStateOf(225f) })
                }
            }.toList()

            Surface(
                modifier = Modifier
                    .background(color = Color.White)
                    .fillMaxWidth()
                    .pointerInput(Unit) {

                        var offsetX = 0.0
                        var offsetY = 0.0

                        /*detectTapGestures {
                            idleAnimtion?.cancel()
                            val anim = animations.random()
                            idleAnimtion = startIdle(Duration.ZERO) {
                                clocks.forEach(anim)
                            }
                        }*/

                        detectDragGestures(
                            onDragStart = {
                                idleAnimtion?.cancel()
                                offsetX = it.x.toDouble()
                                offsetY = it.y.toDouble()
                            },
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                                clocks.forEachIndexed { index, item ->
                                    val origin = Offset(offsetX.toFloat(), offsetY.toFloat())
                                    val target = offsetMap.getOrElse(index) { defaultOffset }
                                    val deg = calcAngle(origin, target)

                                    item.first.value  = deg
                                    item.second.value = deg
                                }
                            },
                            onDragEnd = {
                                val anim = animations.random()
                                idleAnimtion = startTicker(Duration.seconds(1)) {
                                    clocks.forEach(anim)
                                }
                            })
                    }
            ) {

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
