package com.example.todoapp.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todoapp.R
import androidx.navigation.fragment.findNavController
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel : ProfileViewModel by viewModels {
       val db = AppDatabase.getDatabase(requireContext())
        val userRepository = UserRepository(db.userDao())
        ProfileViewModel.Factory(userRepository)
    }

    private var userid: Int = -1;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val userPreferences = UserPreferences(requireContext())
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
                Toast.makeText(context,"dit me may", Toast.LENGTH_SHORT).show()


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