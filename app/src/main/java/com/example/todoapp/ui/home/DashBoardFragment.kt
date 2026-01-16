package com.example.todoapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentDashBoardBinding
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

class DashBoardFragment : Fragment(R.layout.fragment_dash_board) {

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashBoardBinding.bind(view)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHostFragment = childFragmentManager.findFragmentById(
            R.id.nav_host_dashboard
        ) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.calendarFragment,
                R.id.profileFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        if (binding.bottomNav.visibility == View.VISIBLE) return

        binding.bottomNav.apply {
            visibility = View.VISIBLE
            translationY = height.toFloat()
            animate()
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun hideBottomNav() {
        if (binding.bottomNav.visibility == View.GONE) return

        binding.bottomNav.animate()
            .translationY(binding.bottomNav.height.toFloat())
            .setDuration(300)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                binding.bottomNav.visibility = View.GONE
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}