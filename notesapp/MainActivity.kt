package com.example.notesapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.notesapp.ui.NotesViewModel
import com.example.notesapp.ui.screens.AboutScreen
import com.example.notesapp.ui.screens.CreateNoteScreen
import com.example.notesapp.ui.screens.ManageNotesScreen

class MainActivity : ComponentActivity() {
    private val vm: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(AlarmManager::class.java)
            if (!am.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        createNotificationChannel()
        vm.load()

        setContent {
            MaterialTheme {
                var tabIndex by remember { mutableStateOf(0) }
                val notes by vm.notes.collectAsState()
                val ctx = LocalContext.current

                Scaffold(
                    topBar = {
                        TabRow(selectedTabIndex = tabIndex) {
                            Tab(
                                selected = tabIndex == 0,
                                onClick = { tabIndex = 0 },
                                text = { androidx.compose.material3.Text("Створити") }
                            )
                            Tab(
                                selected = tabIndex == 1,
                                onClick = { tabIndex = 1 },
                                text = { androidx.compose.material3.Text("Керування") }
                            )
                            Tab(
                                selected = tabIndex == 2,
                                onClick = { tabIndex = 2 },
                                text = { androidx.compose.material3.Text("Про додаток") }
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(Modifier.padding(paddingValues)) {
                        when (tabIndex) {
                            0 -> CreateNoteScreen(
                                onCreate = { title, description, reminder ->
                                    vm.add(title, description, reminder)
                                }
                            )
                            1 -> ManageNotesScreen(
                                notes = notes,
                                onUpdate = { vm.update(it) },
                                onDelete = { vm.delete(it) },
                                onSync = {
                                    vm.sync { count ->
                                        val message = "Синхронізовано: $count"
                                        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onFetch = {
                                    vm.fetchFromServer { fetchedCount ->
                                        val message = "Отримано з БД: $fetchedCount"
                                        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                            2 -> AboutScreen()
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "notes_reminders",
                "Notes Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
