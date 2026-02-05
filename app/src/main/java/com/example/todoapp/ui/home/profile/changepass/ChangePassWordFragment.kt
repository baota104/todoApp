package com.example.todoapp.ui.home.profile.changepass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentChangePassWordBinding

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePassWordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangePasswordViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repo = UserRepository(db.userDao())
        ChangePasswordViewModel.Factory(repo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePassWordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActions()
        setupObservers()
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnsave.setOnClickListener {
            val currentPass = binding.etCurrentPass.text.toString().trim()
            val newPass = binding.etNewPass.text.toString().trim()
            val confirmPass = binding.etConfirmPass.text.toString().trim()

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userPrefs = UserPreferences(requireContext())
            val userId = userPrefs.getUserId()

            if (userId != -1) {
                viewModel.changePassword(userId!!, currentPass, newPass, confirmPass)
            }
        }

    }

    private fun setupObservers() {
        viewModel.changeResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            result.onFailure { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}