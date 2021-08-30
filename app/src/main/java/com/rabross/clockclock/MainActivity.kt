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
import com.rabross.clockclock.ui.models.Number
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

            val clocks = mutableListOf<Pair<MutableState<Float>, MutableState<Float>>>().apply {
                repeat(clockCount) {
                    add(remember { mutableStateOf(AT_REST) } to remember { mutableStateOf(AT_REST) })
                }
            }

            val offsetMap = mutableMapOf<Int, Offset>()

            var offsetX by remember { mutableStateOf(AT_REST) }
            var offsetY by remember { mutableStateOf(AT_REST) }

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
                                runTimeAnimation(hour, minute, second)
                                offsetX = it.x
                                offsetY = it.y
                            },
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            },
                            onDragEnd = {
                                runIdleAnimations(clocks)
                            }
                        )
                    }
            ) {

                clocks.forEachIndexed { index, item ->
                    val origin = Offset(offsetX, offsetY)
                    val target = offsetMap.getOrElse(index) { Offset(offsetX + 1, offsetY - 1) }
                    val deg = calcAngle(origin, target) % 360

                    item.first.value = deg
                    item.second.value = deg
                }

                val numWidth = 2
                val numHeight = 3
                val digitsHour = hour.value.twoRightMostDigits()
                val digitsMinute = minute.value.twoRightMostDigits()
                val digitsSecond = second.value.twoRightMostDigits()
                clocks.insert(columns, Number.map(digitsHour.first).partClocks, numWidth, numWidth + 1, 1)
                clocks.insert(columns, Number.map(digitsHour.second).partClocks, numWidth, 1, 1)
                clocks.insert(columns, Number.map(digitsMinute.first).partClocks, numWidth, numWidth + 1, numHeight + 1)
                clocks.insert(columns, Number.map(digitsMinute.second).partClocks, numWidth, 1, numHeight + 1)
                clocks.insert(columns, Number.map(digitsSecond.first).partClocks, numWidth, numWidth + 1, numHeight * 2 + 1)
                clocks.insert(columns, Number.map(digitsSecond.second).partClocks, numWidth, 1, numHeight * 2 + 1)
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
    private var timeAnimation: Job? = null
    private val animations = listOf(animDiff, animOpposite, animSame)

    @ExperimentalTime
    private fun startTicker(startDelay: Duration = Duration.seconds(1), onTick: () -> Unit): Job {
        return tickerFlow(Duration.milliseconds(1), startDelay)
            .onEach { onTick() }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    @ExperimentalTime
    private fun runIdleAnimations(clocks: MutableList<Pair<MutableState<Float>, MutableState<Float>>>) {
        timeAnimation?.cancel()
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

    private fun MutableList<Pair<MutableState<Float>, MutableState<Float>>>.insert(
        width: Int,
        set: Array<Pair<Float, Float>>,
        setWidth: Int,
        offsetX: Int = 0,
        offsetY: Int = 0){

        set.forEachIndexed { index, pair ->
            val row = index / setWidth
            val column = index % setWidth
            val mapped = (row + offsetY) * width + column + offsetX

            this[mapped].first.value = pair.first
            this[mapped].second.value = pair.second
        }
    }
}
