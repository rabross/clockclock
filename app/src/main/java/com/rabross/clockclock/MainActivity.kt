package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rabross.clockclock.ui.SixPartClockDisplayRow
import kotlinx.coroutines.delay
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //ClockClockTheme {
            Surface(modifier = Modifier.background(color = Color.White)) {

                val hour = remember { mutableStateOf(0) }
                val minute = remember { mutableStateOf(0) }
                val second = remember { mutableStateOf(0) }

                LaunchedEffect(key1 = Unit, block = {
                    while (true) {
                        delay(1000)
                        val calendar = Calendar.getInstance()
                        hour.value = calendar.get(Calendar.HOUR_OF_DAY)
                        minute.value = calendar.get(Calendar.MINUTE)
                        second.value = calendar.get(Calendar.SECOND)
                    }
                })

                BoxWithConstraints {
                    val clockWidth = this.maxWidth / 2
                    val clockHeight = this.maxHeight / 3
                    Column(verticalArrangement = Arrangement.Top) {
                        SixPartClockDisplayRow(
                            Modifier
                                .width(clockWidth)
                                .height(clockHeight),
                            digits = hour.value.twoRightMostDigits()
                        )
                        SixPartClockDisplayRow(
                            Modifier
                                .width(clockWidth)
                                .height(clockHeight),
                            digits = minute.value.twoRightMostDigits()
                        )
                        SixPartClockDisplayRow(
                            Modifier
                                .width(clockWidth)
                                .height(clockHeight),
                            digits = second.value.twoRightMostDigits()
                        )
                    }
                }
            }
            //}
        }
    }

    private fun Int.twoRightMostDigits(): Pair<Int, Int> {
        if (this == 0) return 0 to 0
        val firstDigit: Int = this % 10
        val secondDigit: Int = if (this < 10) 0 else this / 10 % 10
        return firstDigit to secondDigit
    }
}