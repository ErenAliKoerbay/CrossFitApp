package com.example.crossfitapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.Serializable

class Session(
    initialLabel: String,
    initialTime: String = "05:00"
) : Serializable {
    var label by mutableStateOf(initialLabel)
    var time by mutableStateOf(initialTime)
    var remainingSeconds by mutableStateOf(0)

    init {
        updateFromTime()
    }

    fun updateFromTime() {
        val parts = time.split(":")
        remainingSeconds = if (parts.size == 2) {
            (parts[0].toIntOrNull()?.times(60) ?: 0) + (parts[1].toIntOrNull() ?: 0)
        } else 0
    }
}
