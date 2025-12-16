package com.example.notesapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notesapp.data.local.Note
import com.example.notesapp.data.local.Reminder
import com.example.notesapp.util.DateTimeUtil

@Composable
fun ManageNotesScreen(
    notes: List<Note>,
    onUpdate: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onSync: () -> Unit,
    onFetch: () -> Unit
) {
    var showInfo by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onSync() }) { Text("Синхронізувати з БД") }
                Button(onClick = { onFetch() }) { Text("Витягнути з БД") }
            }
            IconButton(onClick = { showInfo = true }) {
                Icon(Icons.Default.Info, contentDescription = "Інформація")
            }
        }

        if (showInfo) {
            AlertDialog(
                onDismissRequest = { showInfo = false },
                confirmButton = { TextButton(onClick = { showInfo = false }) { Text("OK") } },
                title = { Text("Що роблять кнопки?") },
                text = {
                    Text(
                        "«Синхронізувати з БД» — надсилає локальні нотатки на Flask сервер для збереження у MongoDB.\n\n" +
                                "«Витягнути з БД» — отримує нотатки з MongoDB через Flask і додає їх у локальну базу."
                    )
                }
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes) { note ->
                NoteCard(
                    note = note,
                    onUpdate = onUpdate,
                    onDelete = onDelete
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteCard(note: Note, onUpdate: (Note) -> Unit, onDelete: (Note) -> Unit) {
    var confirmDelete by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(note.title) }
    var desc by remember { mutableStateOf(note.description) }
    var reminderActive by remember { mutableStateOf(note.reminder?.active ?: false) }
    var year by remember { mutableStateOf(note.reminder?.year ?: 2025) }
    var month by remember { mutableStateOf(note.reminder?.month ?: 12) }
    var day by remember { mutableStateOf(note.reminder?.day ?: 15) }
    var hour by remember { mutableStateOf(note.reminder?.hour ?: 17) }
    var minute by remember { mutableStateOf(note.reminder?.minute ?: 30) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(DateTimeUtil.formatDateTime(note.createdAtMillis), style = MaterialTheme.typography.bodySmall)
            Text(desc.take(120), style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { editMode = !editMode }) {
                    Icon(Icons.Default.Edit, contentDescription = "Редагувати")
                }
                IconButton(onClick = { confirmDelete = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Видалити")
                }
            }

            if (editMode) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Заголовок") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Опис") })

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { editMode = false }) { Text("Скасувати") }
                    Button(onClick = {
                        val rem = if (reminderActive) Reminder(true, year, month, day, hour, minute) else null
                        onUpdate(note.copy(title = title.trim(), description = desc.trim(), reminder = rem))
                        editMode = false
                    }) { Text("Підтвердити зміни") }
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete(note) }) { Text("Так") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Ні") } },
            title = { Text("Видалити нотатку?") },
            text = { Text("Цю дію не можна відмінити.") }
        )
    }
}
