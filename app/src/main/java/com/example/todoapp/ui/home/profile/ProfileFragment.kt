package com.example.todoapp.ui.home.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import com.example.todoapp.R
import androidx.navigation.fragment.findNavController
import com.example.todoapp.MainActivity
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentProfileBinding
import kotlin.jvm.java


class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel : ProfileViewModel by viewModels {
       val db = AppDatabase.getDatabase(requireContext())
        val userRepository = UserRepository(db.userDao())
        ProfileViewModel.Factory(userRepository)
    }

    private var userid: Int = -1;
    private lateinit var userPreferences: UserPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userPreferences = UserPreferences(requireContext())
        userid = userPreferences.getUserId()!!
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userid != -1) {
            profileViewModel.getUserbyId(userid)
            profileViewModel.countCompletedTasks(userid)
        }
        setupUi()
        setupObserve()
    }

    private fun setupUi(){
            binding.cardMyProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }
            binding.cardLocation.setOnClickListener {
                Toast.makeText(context,"dit me may", Toast.LENGTH_SHORT).show()
            }
            binding.cardStatistic.setOnClickListener {
                Toast.makeText(context,"dit me may", Toast.LENGTH_SHORT).show()

            }
            binding.cardLogout.setOnClickListener {
                userPreferences.clearSession()

                // 2. Tạo Intent mở lại MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)

                // 3. Cờ quan trọng: Xóa sạch task cũ và tạo task mới
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                // 4. Bắt đầu Activity và đóng cái hiện tại
                startActivity(intent)
                requireActivity().finish()

            }
            binding.cardSettings.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)

            }
    }
    private fun setupObserve(){
        profileViewModel.user.observe(viewLifecycleOwner){user ->
            binddata(user!!)
        }

        profileViewModel.completedTasksCount.observe(viewLifecycleOwner){count ->
            binding.tvTaskCount.text = count.toString() + " tasks completed"
        }

    }
    private fun binddata(user: User){
            binding.tvUserName.text = user.username
            binding.tvJobTitle.text = user.profession ?: "N/A"
            binding.tvLocation.text = user.location ?: "N/A"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}