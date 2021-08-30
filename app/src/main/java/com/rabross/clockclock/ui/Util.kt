package com.rabross.clockclock.ui

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