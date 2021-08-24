package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rabross.clockclock.ui.Number
import com.rabross.clockclock.ui.SixPartClockDisplay
import com.rabross.clockclock.ui.theme.ClockClockTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val numbers = listOf(Number.Zero, Number.One, Number.Two, Number.Three, Number.Four, Number.Five, Number.Six, Number.Seven, Number.Eight, Number.Nine)
            var index = 0
            ClockClockTheme {
                Surface(modifier = Modifier.background(color = Color.White)) {
                    val number = remember { mutableStateOf<Number>(Number.Zero) }

                    LaunchedEffect(key1 = Unit, block = {
                        while (true) {
                            delay(1000)
                            index = if(index.inc() == numbers.size) 0 else index.inc()
                            number.value = numbers[index]
                        }
                    })
                    SixPartClockDisplay(number.value)
                }
            }
        }
    }
}