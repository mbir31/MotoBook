package com.example.motobook.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.R
import com.example.motobook.MainActivity
import com.example.motobook.domain.model.MaintenanceReminder

object MaintenanceNotificationHelper {
    private const val CHANNEL_ID = "maintenance_alerts_channel"
    private const val CHANNEL_NAME = "MotoBook Maintenance Alerts"
    private const val CHANNEL_DESC = "Notifications for due engine oil changes, chain lubes, and motorcycle maintenance"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun checkAndSendMaintenanceNotifications(
        context: Context,
        reminders: List<MaintenanceReminder>,
        currentOdometer: Float?
    ) {
        createNotificationChannel(context)

        val currentOdo = currentOdometer ?: return
        val activeReminders = reminders.filter { !it.isCompleted }

        activeReminders.forEachIndexed { index, reminder ->
            val dueOdo = reminder.dueOdometer
            if (dueOdo != null) {
                val kmRemaining = dueOdo - currentOdo
                val isOverdue = kmRemaining <= 0
                val isDueSoon = kmRemaining in 1f..200f

                if (isOverdue || isDueSoon) {
                    val title = if (isOverdue) {
                        "🚨 Overdue: ${reminder.title}"
                    } else {
                        "⚠️ Due Soon: ${reminder.title}"
                    }

                    val message = if (isOverdue) {
                        "Current Odo: ${currentOdo.toInt()} km. Task was due at ${dueOdo.toInt()} km (${(-kmRemaining).toInt()} km overdue)."
                    } else {
                        "Current Odo: ${currentOdo.toInt()} km. Target: ${dueOdo.toInt()} km (${kmRemaining.toInt()} km remaining)."
                    }

                    sendNotification(context, notificationId = (1000 + index), title = title, message = message)
                }
            } else if (reminder.dueDate != null) {
                val diffDays = ((reminder.dueDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays <= 3) {
                    val title = if (diffDays <= 0) "🚨 Overdue: ${reminder.title}" else "⚠️ Due Soon: ${reminder.title}"
                    val message = if (diffDays <= 0) "Task date has passed!" else "Due in $diffDays days!"
                    sendNotification(context, notificationId = (1000 + index), title = title, message = message)
                }
            }
        }
    }

    private fun sendNotification(context: Context, notificationId: Int, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Missing POST_NOTIFICATIONS permission on Android 13+ handled gracefully
        }
    }
}
