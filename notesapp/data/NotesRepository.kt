package com.example.notesapp.data

import android.content.Context
import com.example.notesapp.data.local.AppDatabase
import com.example.notesapp.data.local.Note
import com.example.notesapp.data.local.Reminder
import com.example.notesapp.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotesRepository(context: Context) {
    private val dao = AppDatabase.get(context).noteDao()
    private val api = NetworkModule.api

    suspend fun getAll(): List<Note> = withContext(Dispatchers.IO) { dao.getAll() }

    suspend fun add(note: Note): Long = withContext(Dispatchers.IO) { dao.insert(note) }

    suspend fun update(note: Note) = withContext(Dispatchers.IO) { dao.update(note) }

    suspend fun delete(note: Note) = withContext(Dispatchers.IO) {

        val deleted = note.copy(isDeleted = true)
        dao.update(deleted)


        try {
            val payload = NotePayload(
                localId = deleted.id,
                title = deleted.title,
                description = deleted.description,
                createdAtMillis = deleted.createdAtMillis,
                reminder = deleted.reminder?.let { r ->
                    ReminderPayload(r.active, r.year, r.month, r.day, r.hour, r.minute)
                },
                isDeleted = true
            )
            api.syncNotes(SyncRequest(notes = listOf(payload)))
        } catch (_: Exception) {
            // можна залогувати або поставити "pending delete"
        }
    }

    suspend fun syncAll(): Int = withContext(Dispatchers.IO) {
        val notes = dao.getAll()
        val payload = notes.map {
            NotePayload(
                localId = it.id,
                title = it.title,
                description = it.description,
                createdAtMillis = it.createdAtMillis,
                reminder = it.reminder?.let { r ->
                    ReminderPayload(r.active, r.year, r.month, r.day, r.hour, r.minute)
                },
                isDeleted = it.isDeleted
            )
        }
        val res = api.syncNotes(SyncRequest(notes = payload))
        if (res.success) {
            notes.forEach { dao.update(it.copy(isSynced = true)) }
        }
        res.syncedCount
    }

    suspend fun fetchAllFromServer(): List<Note> = withContext(Dispatchers.IO) {
        val response = api.fetchNotes()
        if (response.success) {
            val fetchedNotes = response.notes.map {
                Note(
                    id = it.localId,
                    title = it.title,
                    description = it.description,
                    createdAtMillis = it.createdAtMillis,
                    reminder = it.reminder?.let { r ->
                        Reminder(r.active, r.year, r.month, r.day, r.hour, r.minute)
                    },
                    isSynced = true,
                    isDeleted = it.isDeleted
                )
            }
            fetchedNotes.forEach { dao.insert(it) }
            fetchedNotes
        } else {
            emptyList()
        }
    }
}
