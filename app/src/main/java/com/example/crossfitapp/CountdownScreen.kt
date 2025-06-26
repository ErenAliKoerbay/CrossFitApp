package com.example.crossfitapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crossfitapp.Session
import com.example.crossfitapp.fitnessOptions
import com.example.crossfitapp.formatTime
import kotlinx.coroutines.delay

@Composable
fun CountdownScreen(
    sessions: List<Session>,
    onFinish: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var remainingSeconds by remember { mutableStateOf(sessions[0].remainingSeconds) }
    var isRunning by remember { mutableStateOf(true) }
    var isBreakTime by remember { mutableStateOf(false) }
    var actualLabel by remember { mutableStateOf(sessions[0].label) }

    val exercisesCompleted = remember(currentIndex, isBreakTime) {
        if (isBreakTime) currentIndex + 1 else currentIndex
    }

    LaunchedEffect(currentIndex) {
        actualLabel = if (sessions[currentIndex].label == "Random") {
            fitnessOptions.filter { it != "Random" }.random()
        } else {
            sessions[currentIndex].label
        }
    }

    LaunchedEffect(currentIndex, isRunning, isBreakTime) {
        if (!isRunning) return@LaunchedEffect

        remainingSeconds = if (isBreakTime) {
            20
        } else {
            sessions[currentIndex].updateFromTime()
            sessions[currentIndex].remainingSeconds
        }

        while (remainingSeconds > 0 && isRunning) {
            delay(1000)
            remainingSeconds--
        }

        if (remainingSeconds == 0) {
            if (isBreakTime) {
                if (currentIndex < sessions.size - 1) {
                    currentIndex++
                    isBreakTime = false
                    isRunning = true
                } else {
                    onFinish()
                }
            } else {
                isBreakTime = true
                isRunning = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top) // Abstand reduziert
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isBreakTime) "Pause" else actualLabel,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        val imageName = actualLabel.lowercase().replace(" ", "_")
        val context = LocalContext.current
        val imageResId = remember(imageName, context) {
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
        }

        if (imageResId != 0 && !isBreakTime) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp), // Höhe reduziert
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = actualLabel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                )

                Text(
                    text = when (actualLabel) {
                        "Plank" -> "The most common plank is the forearm plank which is held in a push-up-like position, with the body's weight borne on forearms, elbows, and toes."
                        "Jumping Jacks" -> "A jumping jack is performed by jumping to a position with the legs spread wide. The hands go overhead and then return to a position with the feet together and the arms at the sides."
                        "Burpees" -> "Do a squat, jump into a plank and go back up"
                        "Squats" -> "A squat is a strength exercise in which the trainee lowers their hips from a standing position and then stands back up."
                        "Lunges" -> "A lunge can refer to any position of the human body where one leg is positioned forward with knee bent and foot flat on the ground while the other leg is positioned behind"
                        else -> ""
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(Color(0xAA000000))
                        .padding(8.dp)
                )
            }
        }

        Text(
            text = formatTime(remainingSeconds),
            fontSize = 110.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Completed $exercisesCompleted of ${sessions.size} exercises",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LinearProgressIndicator(
                progress = exercisesCompleted / sessions.size.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.Cyan,
                trackColor = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Reduzierter Abstand

        Button(
            onClick = { onFinish() },
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