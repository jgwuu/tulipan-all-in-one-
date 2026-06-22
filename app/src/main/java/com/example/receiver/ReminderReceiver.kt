package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Tiene un pendiente programado"
        val desc = intent.getStringExtra("desc") ?: "Revise su agenda para más detalles"
        val reminderId = intent.getIntExtra("id", 0)

        showNotification(context, reminderId, title, desc)
    }

    private fun showNotification(context: Context, id: Int, title: String, message: String) {
        val channelId = "university_reminders_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel for SDK 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Agenda",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones del sistema para materias, tareas y compromisos universitarios."
                enableLights(true)
                lightColor = 0xFFF9A825.toInt() // Theme accent color
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Action when clicking the notification redirects to MainActivity
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Custom notification alert tone
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setColor(0xFFF9A825.toInt()) // Tulip Yellow hex accent

        notificationManager.notify(id, notificationBuilder.build())
    }
}
