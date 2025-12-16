package com.example.notesapp.data.local

import java.util.Calendar

data class Reminder(
    val active: Boolean,
    val year: Int,
    val month: Int, // 1..12
    val day: Int,
    val hour: Int,
    val minute: Int
) {
    fun toMillis(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}