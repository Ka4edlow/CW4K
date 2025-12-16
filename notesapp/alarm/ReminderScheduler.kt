package com.example.notesapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.notesapp.data.local.Note

class ReminderScheduler(private val context: Context) {
    fun schedule(note: Note) {
        val reminder = note.reminder ?: return
        if (!reminder.active) return

        val triggerAtMillis = reminder.toMillis()
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", note.title)
            putExtra("description", note.description)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            note.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
    }

    fun cancel(noteId: Long) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            noteId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
    }
}
