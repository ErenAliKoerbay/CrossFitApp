package com.example.crossfitapp

import Session
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CrossfitViewModel : ViewModel() {
    val sessions = mutableStateListOf<Session>()
    var currentSessionIndex by mutableStateOf(0)
    var isRunning by mutableStateOf(false)

    fun startCountdown(onFinish: () -> Unit) {
        if (sessions.isEmpty()) return
        val session = sessions[currentSessionIndex]
        session.updateFromTime()
        isRunning = true

        viewModelScope.launch {
            while (session.remainingSeconds > 0 && isRunning) {
                delay(1000)
                session.remainingSeconds -= 1
            }
            isRunning = false
            onFinish()
        }
    }

    fun stopCountdown() {
        isRunning = false
    }
}