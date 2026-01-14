package com.example.todoapp.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.databinding.FragmentLoginBinding
import com.example.todoapp.ui.auth.LoginViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logout.setOnClickListener {
            val prefs = UserPreferences(requireContext())
            prefs.clearSession() // Xóa dữ liệu

            // Quay về màn hình Login
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }
}