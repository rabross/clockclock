package com.rabross.clockclock.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rabross.clockclock.ui.models.Number
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.math.atan2
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ClockViewModel : ViewModel() {

    private val rows = 9
    private val columns = 4
    private val clockCount = rows * columns
    private val atRest = 225f
    private val clockUpdatePeriod = 500.milliseconds

    private val _clocks = mutableStateOf(List(clockCount) { atRest to atRest })
    val clocks: State<List<Pair<Float, Float>>> = _clocks

    private val _hour = mutableIntStateOf(-1)
    val hour: State<Int> = _hour

    private val _minute = mutableIntStateOf(-1)
    val minute: State<Int> = _minute

    private val _second = mutableIntStateOf(-1)
    val second: State<Int> = _second

    private val _isDragging = mutableStateOf(false)
    val isDragging: State<Boolean> = _isDragging

    private var timeAnimationJob: Job? = null
    private val offsetMap = mutableMapOf<Int, Offset>()

    init {
        runTimeAnimation()
    }

    fun onDragStart() {
        _isDragging.value = true
        timeAnimationJob?.cancel()
    }

    fun onDrag(offsetX: Float, offsetY: Float) {
        if (offsetX == -1f || offsetY == -1f) return

        val origin = Offset(offsetX, offsetY)
        _clocks.value = _clocks.value.mapIndexed { index, pair ->
            offsetMap[index]?.let { offset ->
                val deg = calcAngle(origin, offset) % 360
                deg to deg
            } ?: pair
        }
    }

    fun onDragEnd() {
        _isDragging.value = false
        runTimeAnimation()
    }

    fun reportPosition(index: Int, offset: Offset) {
        offsetMap[index] = offset
    }

    private fun runTimeAnimation() {
        timeAnimationJob?.cancel()
        timeAnimationJob = tickerFlow(clockUpdatePeriod)
            .map { Calendar.getInstance() }
            .distinctUntilChanged { old, new -> old.get(Calendar.SECOND) == new.get(Calendar.SECOND) }
            .onEach { calendar ->
                _hour.intValue = calendar.get(Calendar.HOUR_OF_DAY)
                _minute.intValue = calendar.get(Calendar.MINUTE)
                _second.intValue = calendar.get(Calendar.SECOND)
                updateClockFace()
            }
            .launchIn(viewModelScope)
    }

    private fun updateClockFace() {
        val digitsHour = _hour.intValue.twoRightMostDigits()
        val digitsMinute = _minute.intValue.twoRightMostDigits()
        val digitsSecond = _second.intValue.twoRightMostDigits()

        val temp = MutableList(clockCount) { atRest to atRest }
        val numWidth = 2
        val numHeight = 3

        temp.insert(columns, Number.map(digitsHour.first).partClocks, numWidth, numWidth, 0)
        temp.insert(columns, Number.map(digitsHour.second).partClocks, numWidth, 0, 0)
        temp.insert(columns, Number.map(digitsMinute.first).partClocks, numWidth, numWidth, numHeight)
        temp.insert(columns, Number.map(digitsMinute.second).partClocks, numWidth, 0, numHeight)
        temp.insert(columns, Number.map(digitsSecond.first).partClocks, numWidth, numWidth, numHeight * 2)
        temp.insert(columns, Number.map(digitsSecond.second).partClocks, numWidth, 0, numHeight * 2)

        _clocks.value = temp
    }

    private fun calcAngle(origin: Offset, target: Offset): Float {
        val degrees = Math.toDegrees(atan2(target.y - origin.y, target.x - origin.x).toDouble()).toFloat()
        val clockDegrees = degrees - 90
        return if (clockDegrees < 0) clockDegrees + 360 else clockDegrees
    }

    private fun MutableList<Pair<Float, Float>>.insert(
        width: Int,
        set: Array<Pair<Float, Float>>,
        setWidth: Int,
        offsetX: Int = 0,
        offsetY: Int = 0
    ) {
        set.forEachIndexed { index, pair ->
            val row = index / setWidth
            val column = index % setWidth
            val mapped = (row + offsetY) * width + column + offsetX
            if (mapped in indices) {
                this[mapped] = pair
            }
        }
    }
}
