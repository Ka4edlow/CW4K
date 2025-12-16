package com.example.notesapp.data.remote

data class ReminderPayload(
    val active: Boolean,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
)

data class NotePayload(
    val localId: Long,
    val title: String,
    val description: String,
    val createdAtMillis: Long,
    val reminder: ReminderPayload? = null,
    val isDeleted: Boolean = false
)

data class SyncRequest(val notes: List<NotePayload>)
data class SyncResponse(val success: Boolean, val syncedCount: Int)

data class FetchResponse(val success: Boolean, val notes: List<NotePayload>)

data class DeleteRequest(val localIds: List<Long>)
data class BasicResponse(val success: Boolean, val deletedCount: Int)
