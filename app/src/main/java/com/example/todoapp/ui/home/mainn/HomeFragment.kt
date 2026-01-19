package com.example.todoapp.ui.home.mainn

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.ui.adapter.HomeUnimportantTaskAdapter
import com.example.todoapp.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels {
        val context = requireContext()
        val db = AppDatabase.getDatabase(context)
        val userPrefs = UserPreferences(context)
        TaskViewModel.Factory(db.taskDao(), db.subTaskDao(), userPrefs)
    }

    // 1. Khai báo 2 Adapter mới
    private lateinit var priorityAdapter: HomeImportantTaskAdapter
    private lateinit var dailyAdapter: HomeUnimportantTaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val userPrefs = UserPreferences(requireContext())
        taskViewModel.setCurrentUserId(userPrefs.getUserId()!!)

        setupHeader(userPrefs)
        setupRecyclerViews()
        setupObservers()
    }

    private fun setupHeader(userPrefs: UserPreferences) {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd yyyy", Locale.ENGLISH)
        binding.tvDate.text = dateFormat.format(Date())
        binding.tvWelcome.text = "Welcome baodeptrai!" // Nhớ thêm hàm getUserName vào UserPrefs nhé
    }

    private fun setupRecyclerViews() {
        // --- A. PRIORITY ADAPTER (Card Xanh, Ngang) ---
        priorityAdapter = HomeImportantTaskAdapter { item ->
            // Mở Detail
            Toast.makeText(context, "Open Detail: ${item.task.title}", Toast.LENGTH_SHORT).show()
        }

        binding.rvPriorityTasks.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = priorityAdapter
        }

        // --- B. DAILY ADAPTER (Card Trắng, Dọc) ---
        dailyAdapter = HomeUnimportantTaskAdapter(
            onTaskClick = { item ->
                Toast.makeText(context, "Open Detail: ${item.task.title}", Toast.LENGTH_SHORT).show()
            },
            onTaskStatusChanged = { item, newStatus ->
                // Gọi ViewModel update trạng thái ngay tại màn hình Home
                // Bạn cần thêm hàm updateTaskStatus trong ViewModel
                taskViewModel.updateTaskStatus(item.task.taskId, newStatus)
            }
        )

        binding.rvDailyTasks.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = dailyAdapter
            // Quan trọng: Tắt scroll của RV con để NestedScrollView hoạt động mượt
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskViewModel.allTasks.collectLatest { fullList ->

                    // 1. Lọc Important Task (Priority = 2, Chưa hoàn thành)
                    val importantList = fullList.filter {
                        it.task.priority == 2 && !it.task.isCompleted
                    }
                    priorityAdapter.submitList(importantList)

                    // 2. Lọc Daily Task (Priority = 1, Chưa hoàn thành)
                    val dailyList = fullList.filter {
                        it.task.priority == 1 && !it.task.isCompleted
                    }
                    dailyAdapter.submitList(dailyList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}