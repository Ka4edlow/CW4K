package com.example.notesapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.NotesRepository
import com.example.notesapp.data.local.Note
import com.example.notesapp.data.local.Reminder
import com.example.notesapp.alarm.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NotesRepository(app)
    private val scheduler = ReminderScheduler(app)

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    fun load() {
        viewModelScope.launch {
            _notes.value = repo.getAll()
        }
    }

    fun fetchFromServer(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val fetched = repo.fetchAllFromServer()
                onResult(fetched.size)
                load()
            } catch (e: Exception) {
                onResult(0)
            }
        }
    }

    fun add(title: String, description: String, reminder: Reminder?) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val note = Note(
                title = title.trim(),
                description = description.trim(),
                createdAtMillis = now,
                reminder = reminder
            )
            val id = repo.add(note)
            val saved = note.copy(id = id)
            if (reminder?.active == true) scheduler.schedule(saved)
            load()
        }
    }

    fun update(note: Note) {
        viewModelScope.launch {
            repo.update(note)
            if (note.reminder?.active == true) scheduler.schedule(note)
            else scheduler.cancel(note.id)
            load()
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch {
            scheduler.cancel(note.id)
            repo.delete(note)
            load()
        }
    }

    fun sync(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repo.syncAll()
            onResult(count)
            load()
        }
    }
}
