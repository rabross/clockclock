package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.rabross.clockclock.ui.SixPartClockDisplayRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //ClockClockTheme {
            Surface(modifier = Modifier.background(color = Color.White).fillMaxWidth()) {

                val hour = remember { mutableStateOf(-1) }
                val minute = remember { mutableStateOf(-1) }
                val second = remember { mutableStateOf(-1) }

                lifecycleScope.launch {
                    while (true) {
                        delay(1000)
                        val calendar = Calendar.getInstance()
                        hour.value = calendar.get(Calendar.HOUR_OF_DAY)
                        minute.value = calendar.get(Calendar.MINUTE)
                        second.value = calendar.get(Calendar.SECOND)
                    }
                }

                BoxWithConstraints {
                    val sixClockWidth = this.maxWidth / 2
                    val sixClockHeight = this.maxHeight / 3
                    Column(verticalArrangement = Arrangement.Top) {
                        SixPartClockDisplayRow(
                            Modifier
                                .requiredWidthIn(0.dp, sixClockWidth)
                                .requiredHeightIn(0.dp, sixClockHeight),
                            digits = hour.value.twoRightMostDigits()
                        )
                        SixPartClockDisplayRow(
                            Modifier
                                .requiredWidthIn(0.dp, sixClockWidth)
                                .requiredHeightIn(0.dp, sixClockHeight),
                            digits = minute.value.twoRightMostDigits()
                        )
                        SixPartClockDisplayRow(
                            Modifier
                                .requiredWidthIn(0.dp, sixClockWidth)
                                .requiredHeightIn(0.dp, sixClockHeight),
                            digits = second.value.twoRightMostDigits()
                        )
                    }
                }
            }
            //}
        }
    }

    private fun Int.twoRightMostDigits(): Pair<Int, Int> {
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
}