package com.example.crossfitapp.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.crossfitapp.MAX_SESSIONS

@Composable
fun MaxSessionsAlertDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reached the max") },
        text = { Text("You can only create a total of $MAX_SESSIONS rounds.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
