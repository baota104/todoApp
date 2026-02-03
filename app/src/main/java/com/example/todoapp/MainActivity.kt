package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Kích hoạt chế độ tràn viền (Status bar trong suốt)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // XỬ LÝ INTENT TỪ NOTIFICATION
        handleNotificationIntent(intent, navController)

    }

    // Xử lý khi App đang chạy mà bấm thông báo (SingleTop)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Cập nhật intent mới nhất

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        handleNotificationIntent(intent, navController)
    }

    private fun handleNotificationIntent(intent: Intent?, navController: NavController) {
        val navigateTo = intent?.getStringExtra("NAVIGATE_TO")
        val taskId = intent?.getIntExtra("TARGET_TASK_ID", -1) ?: -1

        if (navigateTo == "TASK_DETAIL" && taskId != -1) {
            // Gói ID vào Bundle để gửi cho DashboardFragment
            val bundle = Bundle().apply {
                putInt("DEEP_LINK_TASK_ID", taskId)
            }

            // Điều hướng đến DashboardFragment (kèm gói hàng bundle)
            // Lưu ý: R.id.dashboardFragment là ID trong nav_main.xml
            navController.navigate(R.id.dashBoardFragment, bundle)
        }
    }
}