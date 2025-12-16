package com.example.notesapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtil {
    fun formatDateTime(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}
