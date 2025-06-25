package com.example.crossfitapp

import Session
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class CrossfitWindow : ComponentActivity() {
    companion object {
        const val EXTRA_SESSIONS = "extra_sessions"
        const val EXTRA_CURRENT_INDEX = "extra_current_index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessions = intent.getSerializableExtra(EXTRA_SESSIONS) as? ArrayList<Session> ?: arrayListOf()
        val currentIndex = intent.getIntExtra(EXTRA_CURRENT_INDEX, 0)

        setContent {
            CrossfitCountdownUI(
                sessions = sessions,
                currentIndex = currentIndex,
                onSkip = { finish() },
                onFinish = { finish() }
            )
        }
    }
}

@Composable
fun CrossfitCountdownUI(
    sessions: List<Session>,
    currentIndex: Int,
    onSkip: () -> Unit,
    onFinish: () -> Unit
) {
    val currentSession = sessions.getOrNull(currentIndex)
    if (currentSession == null) {
        onFinish()
        return
    }

    var remainingSeconds by remember { mutableStateOf(currentSession.remainingSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    // Countdown-Logik
    LaunchedEffect(key1 = isRunning) {
        if (isRunning) {
            while (remainingSeconds > 0 && isRunning) {
                delay(1000)
                remainingSeconds--
            }
            if (remainingSeconds == 0) {
                delay(1000)
                onFinish()
            }
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header mit aktuellem Runden-Titel
        Text(
            text = currentSession.label,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(24.dp)
        )

        // Countdown-Anzeige
        Text(
            text = formatTime(remainingSeconds),
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 40.dp)
        )

        // "Runde überspringen"-Button
        Button(
            onClick = {
                isRunning = false
                onSkip()
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(0.7f)
                .height(55.dp)
        ) {
            Text(
                text = "Go Back",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
}
