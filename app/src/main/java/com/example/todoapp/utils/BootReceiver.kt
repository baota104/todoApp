package com.example.todoapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todoapp.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // 1. Giữ Receiver sống lâu hơn một chút để chạy Coroutine
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val dao = db.taskDao()

                    // 2. Lấy các task trong tương lai (chưa xảy ra)
                    // Bạn cần thêm hàm getFutureTasks vào TaskDao (xem bên dưới)
                    val currentTime = System.currentTimeMillis()
                    val futureTasks = dao.getFutureTasks(currentTime)

                    // 3. Đặt lại báo thức cho từng task
                    futureTasks.forEach { task ->
                        AlarmScheduler.scheduleAlarm(context, task)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // 4. Kết thúc
                    pendingResult.finish()
                }
            }
        }
    }
}