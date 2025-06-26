package com.example.crossfitapp.ui

import androidx.compose.runtime.*
import com.example.crossfitapp.*

@Composable
fun CrossfitUI() {
    var showExercisePicker by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf(Screen.Setup) }
    val sessions = remember { mutableStateListOf<Session>() }

    var showMaxSessionsAlert by remember { mutableStateOf(false) }
    var selectedSessionIndex by remember { mutableStateOf<Int?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf(0) }
    var selectedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        if (sessions.isEmpty()) {
            sessions.add(Session("Random"))
        }
    }

    when (currentScreen) {
        Screen.Setup -> {
            SetupScreen(
                sessions = sessions,
                onStart = { currentScreen = Screen.Countdown },
                onEditTime = { index ->
                    selectedSessionIndex = index
                    val parts = sessions[index].time.split(":")
                    selectedMinutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
                    selectedSeconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    showDialog = true
                },
                onAddSession = {
                    if (sessions.size < MAX_SESSIONS) {
                        selectedSessionIndex = null
                        showExercisePicker = true
                    } else {
                        showMaxSessionsAlert = true
                    }
                },
                onDeleteSession = { index ->
                    sessions.removeAt(index)
                },
                onEditLabel = { index ->
                    selectedSessionIndex = index
                    showExercisePicker = true
                }
            )
        }

        Screen.Countdown -> {
            CountdownScreen(
                sessions = sessions,
                onFinish = { currentScreen = Screen.Setup }
            )
        }
    }

    if (showDialog && selectedSessionIndex != null) {
        TimePickerDialog(
            session = sessions[selectedSessionIndex!!],
            minutes = selectedMinutes,
            seconds = selectedSeconds,
            onTimeChanged = { min, sec ->
                selectedMinutes = min
                selectedSeconds = sec
            },
            onConfirm = {
                sessions[selectedSessionIndex!!].time = "%02d:%02d".format(selectedMinutes, selectedSeconds)
                sessions[selectedSessionIndex!!].updateFromTime()
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }

    if (showMaxSessionsAlert) {
        MaxSessionsAlertDialog(
            onDismiss = { showMaxSessionsAlert = false }
        )
    }

    if (showExercisePicker) {
        ExercisePickerDialog(
            onDismiss = {
                showExercisePicker = false
                selectedSessionIndex = null
            },
            onSelect = { option ->
                if (selectedSessionIndex != null) {
                    sessions[selectedSessionIndex!!].label = option
                    selectedSessionIndex = null
                } else {
                    sessions.add(Session(option))
                }
                showExercisePicker = false
            }
        )
    }
}
