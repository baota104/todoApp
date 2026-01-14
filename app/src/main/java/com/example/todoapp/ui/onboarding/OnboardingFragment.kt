package com.example.todoapp.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.example.todoapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import android.widget.TextView

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ánh xạ View (Bạn có thể dùng ViewBinding để gọn hơn)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val btnNext: MaterialButton = view.findViewById(R.id.btnNext)
        val tvSkip: TextView = view.findViewById(R.id.tvSkip)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        // 2. Chuẩn bị dữ liệu (Lấy từ PDF)
        val items = listOf(
            OnboardingItem(
                title = "Easy Time Management",
                description = "With management based on priority and daily tasks, it will give you convenience in managing and determining the tasks that must be done first.",
                imageRes = R.drawable.onboard1 // Thay bằng ảnh 1
            ),
            OnboardingItem(
                title = "Increase Work Effectiveness",
                description = "Time management and the determination of more important tasks will give your job statistics better and always improve.",
                imageRes = R.drawable.onboard2 // Thay bằng ảnh 2
            ),
            OnboardingItem(
                title = "Reminder Notification",
                description = "The advantage of this application is that it provides reminders for you so you don't forget to keep doing your assignments well.",
                imageRes = R.drawable.onboard3 // Thay bằng ảnh 3
            )
        )

        viewPager.adapter = OnboardingAdapter(items)

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // 5. Sự kiện nút Next / Get Started
        btnNext.setOnClickListener {
            if (viewPager.currentItem < items.size - 1) {
                viewPager.currentItem += 1
            } else {
                navigateToLogin()
            }
        }

        // 6. Sự kiện nút Skip
        tvSkip.setOnClickListener {
            navigateToLogin()
        }

        // 7. Đổi chữ nút bấm ở trang cuối
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == items.size - 1) {
                    btnNext.text = "Get Started"
                } else {
                    btnNext.text = "Next"
                }
            }
        })
    }

    private fun navigateToLogin() {
        // Điều hướng sang màn hình đăng nhập
        // Đảm bảo bạn đã tạo action trong nav_graph.xml
        findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
    }
}