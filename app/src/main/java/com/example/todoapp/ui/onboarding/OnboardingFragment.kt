package com.example.todoapp.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.example.todoapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import android.widget.TextView
import com.example.todoapp.databinding.FragmentLoginBinding
import com.example.todoapp.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {
    private var _binding:FragmentOnboardingBinding? = null
    private val  binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = binding.viewPager
        val btnNext: MaterialButton = binding.btnNext
        val tvSkip: TextView = binding.tvSkip
        val tabLayout: TabLayout = binding.tabLayout

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

        btnNext.setOnClickListener {
            if (viewPager.currentItem < items.size - 1) {
                viewPager.currentItem += 1
            } else {
                navigateToLogin()
            }
        }

        tvSkip.setOnClickListener {
            navigateToLogin()
        }

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