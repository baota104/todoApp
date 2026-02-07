package com.example.todoapp.ui.home.mainn

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.ui.adapter.HomeUnimportantTaskAdapter
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel:HomeViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val userPreferences = UserPreferences(requireContext())
        val categoryRepository = CategoryRepository(db.categoryDao())
        val taskRepository = TaskRepository(db.taskDao(),db.subTaskDao())
        val userRepository = UserRepository(db.userDao())
        HomeViewModel.Factory(categoryRepository,taskRepository,userRepository,userPreferences)
    }

    private lateinit var priorityAdapter: HomeImportantTaskAdapter
    private lateinit var dailyAdapter: HomeUnimportantTaskAdapter

    private var allTasks: List<TaskPopulated> = listOf()
    private var imtask: List<TaskPopulated> = listOf()
    private var untask: List<TaskPopulated> = listOf()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val userPrefs = UserPreferences(requireContext())
        homeViewModel.insertDefaultCategories()
        homeViewModel.getUser()
        setupRecyclerViews()
        setupObservers()
    }


    private fun setupHeader(user: User) {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd yyyy", Locale.ENGLISH)
        binding.tvDate.text = dateFormat.format(Date())
        binding.tvWelcome.text = "Welcome back, ${user.username}!" // Nhớ thêm hàm getUserName vào UserPrefs nhé
    }

    private fun setupRecyclerViews() {
        priorityAdapter = HomeImportantTaskAdapter { item ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToTaskDetailFragment(item.task.taskId)
            findNavController().navigate(action)
            Toast.makeText(context, "Open Detail: ${item.task.title}", Toast.LENGTH_SHORT).show()
        }
        binding.ivNoti.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        binding.rvPriorityTasks.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = priorityAdapter
        }

        dailyAdapter = HomeUnimportantTaskAdapter(
            onTaskClick = { item ->
                val action = HomeFragmentDirections.actionHomeFragmentToTaskDetailFragment(item.task.taskId)
                findNavController().navigate(action)
                Toast.makeText(context, "Open Detail: ${item.task.title}", Toast.LENGTH_SHORT).show()
            },
            onTaskStatusChanged = { item, newStatus ->

            }
        )

        binding.rvDailyTasks.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = dailyAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
            homeViewModel.taskList.observe(viewLifecycleOwner){list ->
                allTasks = list

                val importantList = list.filter { taskWithCategory ->
                    taskWithCategory.task.priority >= 2 && !taskWithCategory.task.isCompleted
                }

                val dailyList = list.filter { taskWithCategory ->
                    taskWithCategory.task.priority < 2
                }

                imtask = importantList
                untask = dailyList

                priorityAdapter.submitList(importantList)
                dailyAdapter.submitList(dailyList)

            }
        homeViewModel.user.observe(viewLifecycleOwner) { user ->
           setupHeader(user)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}