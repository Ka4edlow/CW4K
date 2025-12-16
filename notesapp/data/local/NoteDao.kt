package com.example.notesapp.data.local

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY createdAtMillis DESC")
    suspend fun getAll(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
