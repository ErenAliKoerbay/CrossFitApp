package com.example.crossfitapp.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.crossfitapp.TimePicker
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TimePickerDialog(
    session: com.example.crossfitapp.Session,
    minutes: Int,
    seconds: Int,
    onTimeChanged: (Int, Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick time for ${session.label}") },
        text = {
            TimePicker(
                minutes = minutes,
                seconds = seconds,
                onTimeChange = onTimeChanged
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
