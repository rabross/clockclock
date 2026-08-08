package com.rabross.clockclock.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

internal val randomAngle
    get() = Math.random().toFloat() * 360f

@ExperimentalTime
internal fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (currentCoroutineContext().isActive) {
        emit(Unit)
        delay(period)
    }
}

fun Int.twoRightMostDigits(): Pair<Int, Int> {
    return when (this) {
        -1 -> -1 to -1
        0 -> 0 to 0
        else -> {
            val firstDigit: Int = this % 10
            val secondDigit: Int = if (this < 10) 0 else this / 10 % 10
            firstDigit to secondDigit
        }
    }
}

@Composable
fun rememberShortestPathDegree(target: Float): Float {
    var accumulated by remember { mutableFloatStateOf(target) }

    LaunchedEffect(target) {
        val diff = (target - accumulated) % 360f
        // Normalize the difference to [-180, 180]
        val shortestDiff = when {
            diff > 180f -> diff - 360f
            diff < -180f -> diff + 360f
            else -> diff
        }
        accumulated += shortestDiff
    }

    return accumulated
}