package com.example.crossfitapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crossfitapp.Session

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
