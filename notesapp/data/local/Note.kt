package com.example.notesapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val createdAtMillis: Long,
    val reminder: Reminder? = null,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)
