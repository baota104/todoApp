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
        // 1. Nhận dữ liệu (Chấp nhận trường hợp chỉ có Title)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Công việc"

        // Nếu không truyền ID, ta dùng mã băm của Title để tạo ra một số nguyên làm ID
        // Điều này giúp các thông báo khác tiêu đề sẽ không đè lên nhau.
        val taskIdExtra = intent.getIntExtra("TASK_ID", -1)
        val notificationId = if (taskIdExtra != -1) taskIdExtra else taskTitle.hashCode()

        val message = "Đã đến hạn làm việc: $taskTitle"
        Log.d("AlarmReceiver", "Báo thức đã nổ! ID: $taskTitle - Title: $taskTitle")
        // 2. Xử lý lưu vào Database (Chạy ngầm để không chặn UI)
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)

                // Tạo đối tượng thông báo để lưu lịch sử
                val newNoti = Notification(
                    title = "Nhắc nhở công việc",
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    type = 3 // Loại 3: Cảnh báo (Warning)
                )

                db.notificationDao().InsertNotification(newNoti)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }

        // 3. Hiển thị thông báo ra màn hình
        showNotification(context, notificationId, taskTitle, message)
    }

    private fun showNotification(context: Context, notiId: Int, title: String, message: String) {
        val channelId = "todo_app_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo Channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở công việc",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo cho các công việc đến hạn"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // --- XỬ LÝ MỞ MÀN HÌNH KHÁC KHI CLICK ---
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Gửi tín hiệu để MainActivity biết cần mở màn nào
            // Ví dụ: Muốn mở màn Detail thì gửi ID
            putExtra("NAVIGATE_TO", "TASK_DETAIL")
            putExtra("TARGET_TASK_ID", notiId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notiId, // RequestCode khác nhau để không bị ghi đè Intent
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build Notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notifications) // Đảm bảo bạn có icon này (trong suốt, màu trắng)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Gắn sự kiện click
            .setAutoCancel(true) // Click xong tự biến mất

        notificationManager.notify(notiId, builder.build())
    }
}