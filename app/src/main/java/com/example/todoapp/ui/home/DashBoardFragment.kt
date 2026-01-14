// ui/home/DashBoardFragment.kt

package com.example.todoapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentDashBoardBinding

class DashBoardFragment : Fragment(R.layout.fragment_dash_board) {

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashBoardBinding.bind(view)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // 1. Tìm NavHostFragment con
        val navHostFragment = childFragmentManager.findFragmentById(
            R.id.nav_host_dashboard
        ) as NavHostFragment

        // 2. Lấy NavController của nó
        val navController = navHostFragment.navController

        // 3. Kết nối BottomNav với NavController
        // Lưu ý quan trọng: ID của item trong menu phải TRÙNG KHỚP với ID của fragment trong nav_dashboard.xml
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}