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

    private val _columns = mutableIntStateOf(4)
    val columns: State<Int> = _columns

    private val _rows = mutableIntStateOf(9)
    val rows: State<Int> = _rows

    private val clockUpdatePeriod = 500.milliseconds

    private val _clocks = mutableStateOf(List(4 * 9) { 0f to 0f })
    val clocks: State<List<Pair<Float, Float>>> = _clocks

    private val _hour = mutableIntStateOf(-1)
    private val _minute = mutableIntStateOf(-1)
    private val _second = mutableIntStateOf(-1)

    private val _isDragging = mutableStateOf(false)
    val isDragging: State<Boolean> = _isDragging

    private var timeAnimationJob: Job? = null
    private val offsetMap = mutableMapOf<Int, Offset>()

    init {
        runTimeAnimation()
    }

    fun updateDimensions(cols: Int, rows: Int) {
        if (_columns.intValue != cols || _rows.intValue != rows) {
            _columns.intValue = cols
            _rows.intValue = rows
            updateClockFace()
        }
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

        val cols = _columns.intValue
        val rows = _rows.intValue
        val isLandscape = cols > rows
        val clockCount = cols * rows

        val temp = MutableList(clockCount) { 0f to 0f }
        val numWidth = 2
        val numHeight = 3

        if (isLandscape) {
            // Landscape layout: HH MM SS side-by-side (12 columns)
            temp.insert(cols, Number.map(digitsHour.second).partClocks, numWidth, 0, 0)
            temp.insert(cols, Number.map(digitsHour.first).partClocks, numWidth, 2, 0)

            temp.insert(cols, Number.map(digitsMinute.second).partClocks, numWidth, 4, 0)
            temp.insert(cols, Number.map(digitsMinute.first).partClocks, numWidth, 6, 0)

            temp.insert(cols, Number.map(digitsSecond.second).partClocks, numWidth, 8, 0)
            temp.insert(cols, Number.map(digitsSecond.first).partClocks, numWidth, 10, 0)
        } else {
            // Portrait layout: HH, MM, SS stacked
            temp.insert(cols, Number.map(digitsHour.first).partClocks, numWidth, numWidth, 0)
            temp.insert(cols, Number.map(digitsHour.second).partClocks, numWidth, 0, 0)
            temp.insert(cols, Number.map(digitsMinute.first).partClocks, numWidth, numWidth, numHeight)
            temp.insert(cols, Number.map(digitsMinute.second).partClocks, numWidth, 0, numHeight)
            temp.insert(cols, Number.map(digitsSecond.first).partClocks, numWidth, numWidth, numHeight * 2)
            temp.insert(cols, Number.map(digitsSecond.second).partClocks, numWidth, 0, numHeight * 2)
        }

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
