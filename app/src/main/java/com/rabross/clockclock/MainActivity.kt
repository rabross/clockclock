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
import com.rabross.clockclock.ui.PartClock
import com.rabross.clockclock.ui.SixPartClockDisplay
import com.rabross.clockclock.ui.theme.ClockClockTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockClockTheme {
                Surface(modifier = Modifier.background(color = Color.White)) {
                    val hour = remember { mutableStateOf(randomAngle) }
                    val minute = remember { mutableStateOf(randomAngle) }
                    LaunchedEffect(key1 = Unit, block = {
                        while (true) {
                            delay(1000)
                            hour.value = randomAngle
                            minute.value = randomAngle
                        }
                    })
                    SixPartClockDisplay(Array(6) {
                        hour.value to minute.value
                    }, Modifier)
                }
            }
        }
    }

    private val randomAngle
        get() = Math.random().toFloat() * 360f
}