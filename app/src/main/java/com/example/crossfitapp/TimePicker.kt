package com.example.crossfitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TimePicker(
    minutes: Int,
    seconds: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    Row {
        TimeDropdown("Minutes", 60, minutes) { onTimeChange(it, seconds) }
        Spacer(Modifier.width(12.dp))
        TimeDropdown("Seconds", 60, seconds) { onTimeChange(minutes, it) }
    }
}

@Composable
fun TimeDropdown(label: String, maxValue: Int, selectedValue: Int, onValueSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = (0 until maxValue).toList()

    Box {
        Text(
            text = "$label: ${selectedValue.toString().padStart(2, '0')}",
            modifier = Modifier
                .background(Color.LightGray, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(16.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value.toString().padStart(2, '0')) },
                    onClick = {
                        onValueSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}
