package com.example.notesapp.data.local

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(Reminder::class.java)

    @TypeConverter
    fun reminderToString(reminder: Reminder?): String? {
        return reminder?.let { adapter.toJson(it) }
    }

    @TypeConverter
    fun stringToReminder(json: String?): Reminder? {
        return json?.let { adapter.fromJson(it) }
    }
}