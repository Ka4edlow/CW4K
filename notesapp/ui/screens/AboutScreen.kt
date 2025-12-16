package com.example.notesapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(Modifier.padding(16.dp)) {
        Text("Про розробника", style = MaterialTheme.typography.titleLarge)
        Text("Ім'я: Сагач Сергій ТВ-23", style = MaterialTheme.typography.bodyLarge)
        Text("Контакти: sahach@gmail.com | +380111111111", style = MaterialTheme.typography.bodyMedium)
        Text("Опис: Додаток для нотаток з офлайн зберіганням (Room), нагадуваннями (AlarmManager) і синхронізацією через Flask до MongoDB.", style = MaterialTheme.typography.bodyMedium)
        Text("Технології: Kotlin, Jetpack Compose, Room, Retrofit/Moshi, Flask, PyMongo, AES шифрування.", style = MaterialTheme.typography.bodyMedium)
    }
}
