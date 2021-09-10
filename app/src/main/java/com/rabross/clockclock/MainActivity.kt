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
import com.rabross.clockclock.ui.twoRightMostDigits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.math.atan2
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import com.rabross.clockclock.ui.models.Number

private const val AT_REST = 225f

class MainActivity : ComponentActivity() {

    private val rows = 11
    private val columns = 6
    private val clockCount = rows * columns

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //ClockClockTheme {

            val offsetMap = mutableMapOf<Int, Offset>()

            val clocks = remember { mutableStateOf(mutableListOf<Pair<Float, Float>>().apply {
                    repeat(clockCount) {
                        add(AT_REST to AT_REST)
                    }
                }.toList()
            )}

            var offsetX by remember { mutableStateOf(-1f) }
            var offsetY by remember { mutableStateOf(-1f) }

            val hour = remember { mutableStateOf(-1) }
            val minute = remember { mutableStateOf(-1) }
            val second = remember { mutableStateOf(-1) }

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
                                timeAnimation?.cancel()
                                offsetX = it.x
                                offsetY = it.y
                            },
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y

                                clocks.value = clocks.value.mapIndexed { index, pair ->
                                    offsetMap[index]?.let { offset ->
                                        if(offsetX != -1f && offsetY != -1f) {
                                            val origin = Offset(offsetX, offsetY)
                                            val deg = calcAngle(origin, offset) % 360
                                            deg to deg
                                        } else pair
                                    } ?: pair
                                }
                            },
                            onDragEnd = {
                                runTimeAnimation(hour, minute, second)
                                //runIdleAnimations(clocks)
                            }
                        )
                    }
            ) {

                val numWidth = 2
                val numHeight = 3
                val digitsHour = hour.value.twoRightMostDigits()
                val digitsMinute = minute.value.twoRightMostDigits()
                val digitsSecond = second.value.twoRightMostDigits()

                if(timeAnimation?.isActive == true) {

                    val temp = clocks.value.toMutableList()

                    temp.insert(columns, Number.map(digitsHour.first).partClocks, numWidth, numWidth + 1, 1)
                    temp.insert(columns, Number.map(digitsHour.second).partClocks, numWidth, 1, 1)
                    temp.insert(columns, Number.map(digitsMinute.first).partClocks, numWidth, numWidth + 1, numHeight + 1)
                    temp.insert(columns, Number.map(digitsMinute.second).partClocks, numWidth, 1, numHeight + 1)
                    temp.insert(columns, Number.map(digitsSecond.first).partClocks, numWidth, numWidth + 1, numHeight * 2 + 1)
                    temp.insert(columns, Number.map(digitsSecond.second).partClocks, numWidth, 1, numHeight * 2 + 1)

                    clocks.value = temp
                }

                PartClockGridDisplay(clocks.value, columns, rows) { index, offset ->
                    offsetMap[index] = offset
                }
            }

            runTimeAnimation(hour, minute, second)
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
            item.first.value = (item.first.value + 0.025f) % 360
            item.second.value = (item.second.value + 0.05f) % 360
        }

    private val animSame: (Pair<MutableState<Float>, MutableState<Float>>) -> Unit =
        { item ->
            item.first.value = (item.first.value + 0.05f) % 360
            item.second.value = (item.second.value + 0.05f) % 360
        }

    private val animOpposite: (Pair<MutableState<Float>, MutableState<Float>>) -> Unit =
        { item ->
            item.first.value = (item.first.value - 0.025f) % 360
            item.second.value = (item.second.value + 0.05f) % 360
        }

    private var idleAnimtion: Job? = null
    private var timeAnimation: Job? = null
    private val animations = listOf(animDiff, animOpposite)

    @ExperimentalTime
    private fun startTicker(startDelay: Duration = Duration.seconds(1), onTick: () -> Unit): Job {
        return tickerFlow(Duration.milliseconds(1), startDelay)
            .onEach { onTick() }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    @ExperimentalTime
    private fun runIdleAnimations(clocks: MutableList<Pair<MutableState<Float>, MutableState<Float>>>) {
        timeAnimation?.cancel()
        idleAnimtion?.cancel()
        val anim = animations.random()
        idleAnimtion = startTicker(Duration.seconds(1)) {
            clocks.forEach(anim)
        }
    }

    @ExperimentalTime
    private fun runTimeAnimation(
        hour: MutableState<Int>,
        minute: MutableState<Int>,
        second: MutableState<Int>
    ) {
        idleAnimtion?.cancel()
        timeAnimation?.cancel()
        timeAnimation = tickerFlow(Duration.milliseconds(500))
            .map { Calendar.getInstance() }
            .distinctUntilChanged { old, new ->
                old.get(Calendar.SECOND) == new.get(Calendar.SECOND)
            }
            .onEach { calendar ->
                hour.value = calendar.get(Calendar.HOUR_OF_DAY)
                minute.value = calendar.get(Calendar.MINUTE)
                second.value = calendar.get(Calendar.SECOND)
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun hasEnoughSpaceForFullClock(width: Int, height: Int): Boolean {
        val clockWidth = 4
        val clockHeight = 9
        return rectCount(width, height, clockWidth, clockHeight) > 0
    }

    private fun rectCount(n: Int, m: Int, x: Int, y: Int): Int {
        return (n-x+1)*(m-y+1)
    }

    private fun MutableList<Pair<Float, Float>>.insert(
        width: Int,
        set: Array<Pair<Float, Float>>,
        setWidth: Int,
        offsetX: Int = 0,
        offsetY: Int = 0) {

        set.forEachIndexed { index, pair ->
            val row = index / setWidth
            val column = index % setWidth
            val mapped = (row + offsetY) * width + column + offsetX

            this[mapped] = pair
        }
    }
}
