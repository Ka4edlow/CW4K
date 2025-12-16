package com.example.notesapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.notesapp.data.local.Reminder

@Composable
fun CreateNoteScreen(onCreate: (String, String, Reminder?) -> Unit) {
    val ctx = LocalContext.current
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    var reminderActive by remember { mutableStateOf(false) }
    var year by remember { mutableStateOf(2025) }
    var month by remember { mutableStateOf(12) }
    var day by remember { mutableStateOf(15) }
    var hour by remember { mutableStateOf(17) }
    var minute by remember { mutableStateOf(30) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Створити нотатку", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Заголовок") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description, onValueChange = { description = it },
            label = { Text("Опис") }, modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Checkbox(checked = reminderActive, onCheckedChange = { reminderActive = it })
            Text("Нагадування активне")
        }
        if (reminderActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = year.toString(),
                    onValueChange = { it.toIntOrNull()?.let { year = it } },
                    label = { Text("Рік") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = month.toString(),
                    onValueChange = { it.toIntOrNull()?.let { month = it.coerceIn(1,12) } },
                    label = { Text("Місяць") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = day.toString(),
                    onValueChange = { it.toIntOrNull()?.let { day = it.coerceIn(1,31) } },
                    label = { Text("День") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = hour.toString(),
                    onValueChange = { it.toIntOrNull()?.let { hour = it.coerceIn(0,23) } },
                    label = { Text("Година") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minute.toString(),
                    onValueChange = { it.toIntOrNull()?.let { minute = it.coerceIn(0,59) } },
                    label = { Text("Хвилини") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = {
                if (title.text.isBlank()) {
                    Toast.makeText(ctx, "Вкажіть заголовок", Toast.LENGTH_SHORT).show()
                } else {
                    val rem = if (reminderActive) Reminder(true, year, month, day, hour, minute) else null
                    onCreate(title.text, description.text, rem)
                    title = TextFieldValue("")
                    description = TextFieldValue("")
                    reminderActive = false
                    Toast.makeText(ctx, "Нотатку створено", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Зберегти")
            }
        }
    }
}
