package com.example.todoapp.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.local.UserPreferences

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences(requireContext())

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                if (userPreferences.isLoggedIn()) {
                    findNavController().navigate(R.id.action_splashFragment_to_dashBoardFragment)
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                }
            }
        }, 2000)
    }
}