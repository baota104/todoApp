package com.example.todoapp.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.todoapp.data.entity.Task

object AlarmScheduler {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = task.startDate ?: return

        if (triggerTime <= System.currentTimeMillis()) return

        // 2. [QUAN TRỌNG] Kiểm tra quyền trên Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmScheduler", "Chưa có quyền đặt báo thức chính xác!")
                // Tùy chọn: Mở cài đặt để user cấp quyền hoặc dùng setWindow/setAndAllowWhileIdle thay thế
                return
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_ID", task.taskId)
            putExtra("TASK_TITLE", task.title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Đặt báo thức
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Lỗi bảo mật: ${e.message}")
        }
    }

    fun cancelAlarm(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}