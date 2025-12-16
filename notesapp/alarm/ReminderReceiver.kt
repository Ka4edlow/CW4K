package com.example.notesapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notesapp.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Нагадування"
        val desc = intent.getStringExtra("description") ?: ""

        val channelId = "notes_reminders"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(desc.take(80))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
    }
}

