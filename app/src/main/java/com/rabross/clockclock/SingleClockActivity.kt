package com.rabross.clockclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.rabross.clockclock.ui.PartClock
import com.rabross.clockclock.ui.tickerFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class SingleClockActivity : ComponentActivity() {
    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val hour = remember { mutableStateOf(0f) }
            val minute = remember { mutableStateOf(180f) }

            Surface(Modifier.fillMaxSize()) {
                PartClock(hour.value, minute.value)
            }

            runTimeAnimation(hour, minute)
        }
    }

    @ExperimentalTime
    private fun runTimeAnimation(
        hour: MutableState<Float>,
        minute: MutableState<Float>
    ) {
        tickerFlow(Duration.milliseconds(2000))
            .map { Calendar.getInstance() }
            .distinctUntilChanged { old, new ->
                old.get(Calendar.SECOND) == new.get(Calendar.SECOND)
            }
            .onEach { calendar ->
                hour.value = if (hour.value == 90f) -90f else 90f
                minute.value = if (minute.value == 90f) -90f else 90f
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }
}