import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import java.io.Serializable

const val MAX_SESSIONS = 5
val fitnessOptions = listOf("Plank", "Jumping Jacks", "Burpees", "Squats", "Lunges", "Random")

enum class Screen {
    Setup,
    Countdown
}

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


class MainWindow : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrossfitUI()
        }
    }
}

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
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Pick time for ${sessions[selectedSessionIndex!!].label}") },
            text = {
                TimePicker(
                    minutes = selectedMinutes,
                    seconds = selectedSeconds,
                    onTimeChange = { min, sec ->
                        selectedMinutes = min
                        selectedSeconds = sec
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    sessions[selectedSessionIndex!!].time = "%02d:%02d".format(selectedMinutes, selectedSeconds)
                    sessions[selectedSessionIndex!!].updateFromTime()
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showMaxSessionsAlert) {
        AlertDialog(
            onDismissRequest = { showMaxSessionsAlert = false },
            title = { Text("Reached the max") },
            text = { Text("You can only create a total of $MAX_SESSIONS rounds.") },
            confirmButton = {
                TextButton(onClick = { showMaxSessionsAlert = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showExercisePicker) {
        AlertDialog(
            onDismissRequest = {
                showExercisePicker = false
                selectedSessionIndex = null
            },
            title = { Text("Pick an exercise") },
            text = {
                Column {
                    fitnessOptions.forEach { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedSessionIndex != null) {
                                        sessions[selectedSessionIndex!!].label = option
                                        selectedSessionIndex = null
                                    } else {
                                        sessions.add(Session(option))
                                    }
                                    showExercisePicker = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showExercisePicker = false
                    selectedSessionIndex = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

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
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Übungsname oben, größer und zentriert
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

        Spacer(modifier = Modifier.weight(1f))

        // Countdown groß und zentriert
        Text(
            text = formatTime(remainingSeconds),
            fontSize = 110.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Progress direkt unter dem Timer
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

        Spacer(modifier = Modifier.weight(1f))

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


@Composable
fun SetupScreen(
    sessions: MutableList<Session>,
    onStart: () -> Unit,
    onEditTime: (Int) -> Unit,
    onAddSession: () -> Unit,
    onDeleteSession: (Int) -> Unit,
    onEditLabel: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Crossfit",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray)
                .padding(vertical = 8.dp)
        ) {
            itemsIndexed(sessions) { index, session ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .background(
                            color = if (index % 2 == 0) Color(0xFFE0F7FA) else Color(0xFFB2EBF2),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = session.label,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clickable { onEditLabel(index) }
                    )

                    Text(
                        text = session.time,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable { onEditTime(index) }
                    )

                    Text(
                        text = "X",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { onDeleteSession(index) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { onAddSession() },
            shape = CircleShape,
            containerColor = Color.LightGray,
            modifier = Modifier.padding(vertical = 20.dp)
        ) {
            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { onStart() },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(0.7f)
                .height(55.dp)
        ) {
            Text("Start Workout", fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun TimePicker(minutes: Int, seconds: Int, onTimeChange: (Int, Int) -> Unit) {
    var expandedMin by remember { mutableStateOf(false) }
    var expandedSec by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Text(
                text = "%02d".format(minutes),
                modifier = Modifier
                    .clickable { expandedMin = true }
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            DropdownMenu(expanded = expandedMin, onDismissRequest = { expandedMin = false }) {
                (0..59).forEach { min ->
                    DropdownMenuItem(text = { Text("%02d".format(min)) }, onClick = {
                        onTimeChange(min, seconds)
                        expandedMin = false
                    })
                }
            }
        }

        Text(":", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 8.dp))

        Box {
            Text(
                text = "%02d".format(seconds),
                modifier = Modifier
                    .clickable { expandedSec = true }
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            DropdownMenu(expanded = expandedSec, onDismissRequest = { expandedSec = false }) {
                (0..59).forEach { sec ->
                    DropdownMenuItem(text = { Text("%02d".format(sec)) }, onClick = {
                        onTimeChange(minutes, sec)
                        expandedSec = false
                    })
                }
            }
        }
    }
}

fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
