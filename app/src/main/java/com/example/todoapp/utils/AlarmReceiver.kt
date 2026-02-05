package com.example.todoapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Task"

        val taskIdExtra = intent.getIntExtra("TASK_ID", -1)
        val notificationId = if (taskIdExtra != -1) taskIdExtra else taskTitle.hashCode()

        val message = "it is time to complete this task: $taskTitle"
        Log.d("AlarmReceiver", "Báo thức đã nổ! ID: $taskTitle - Title: $taskTitle")

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)

                val newNoti = Notification(
                    title = "task reminder",
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    type = 3 // warning
                )
                db.notificationDao().InsertNotification(newNoti)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }

        showNotification(context, notificationId, taskTitle, message)
    }

    private fun showNotification(context: Context, notiId: Int, title: String, message: String) {
        val channelId = "todo_app_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Todo App Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Todo App"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // xu li mo man hinh khac
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // gửi tín hiệu để MainActivity biết cần mở màn nào
            // ví dụ: Muốn mở màn Detail thì gửi ID
            putExtra("NAVIGATE_TO", "TASK_DETAIL")
            putExtra("TARGET_TASK_ID", notiId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notiId, // RequestCode khac nhau de khong ghi de intent
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build Notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // su kien click
            .setAutoCancel(true) // click xong bien mat

        notificationManager.notify(notiId, builder.build())
    }
}