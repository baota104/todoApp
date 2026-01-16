package com.example.todoapp.ui.home.calendar

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.databinding.FragmentCalendarBinding
import com.example.todoapp.ui.calendar.CalendarAdapter
import com.example.todoapp.ui.calendar.CalendarDailyAdapter
import com.example.todoapp.ui.calendar.CalendarPriorityAdapter
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    // Khai báo 2 Adapter
    private lateinit var priorityAdapter: CalendarPriorityAdapter
    private lateinit var dailyAdapter: CalendarDailyAdapter

    // Adapter cho dải lịch ngang
    private lateinit var calendarStripAdapter: CalendarAdapter

    private var isPriorityTabSelected = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCalendarBinding.bind(view)
        val userPreferences = UserPreferences(requireContext())
        initAdapters()
        setupCalendarStrip()
        setupTabs()
//        setupAddTaskButton()

        // Mặc định chọn tab Priority khi mở màn hình
        selectTab(true)

        binding.btnAddTask.setOnClickListener {

            findNavController().navigate(R.id.action_calendarFragment_to_addTaskFragment)
        }
    }

    private fun initAdapters() {
        // Init Adapter Priority
        priorityAdapter = CalendarPriorityAdapter { task ->
            Toast.makeText(context, "Clicked: ${task.title}", Toast.LENGTH_SHORT).show()
        }

        // Init Adapter Daily
        dailyAdapter = CalendarDailyAdapter { task ->
            Toast.makeText(context, "Clicked: ${task.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupCalendarStrip() {
        calendarStripAdapter = CalendarAdapter { selectedDate ->
            Toast.makeText(context, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
        }

        binding.rvCalendarStrip.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = calendarStripAdapter
        }

        // Tạo dữ liệu giả cho dải lịch (30 ngày tới)
        val calendar = Calendar.getInstance()
        val dateList = mutableListOf<Date>()
        for (i in 0..30) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        calendarStripAdapter.submitList(dateList)
    }

    private fun setupTabs() {
        // Bắt sự kiện click vào TextView tab
        binding.tvTabPriority.setOnClickListener {
            if (!isPriorityTabSelected) selectTab(true)
        }

        binding.tvTabDaily.setOnClickListener {
            if (isPriorityTabSelected) selectTab(false)
        }
    }

    private fun selectTab(isPriority: Boolean) {
        isPriorityTabSelected = isPriority
        val context = requireContext()

        if (isPriority) {
            // --- UI: Active Priority ---
            binding.tvTabPriority.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            binding.indicatorPriority.visibility = View.VISIBLE

            binding.tvTabDaily.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            binding.indicatorDaily.visibility = View.INVISIBLE

            // --- Logic: Swap Adapter ---
            binding.rvTasks.adapter = priorityAdapter

            // --- Data: Load Dummy Priority Data ---
            loadDummyPriorityData()

        } else {
            // --- UI: Active Daily ---
            binding.tvTabPriority.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            binding.indicatorPriority.visibility = View.INVISIBLE

            binding.tvTabDaily.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            binding.indicatorDaily.visibility = View.VISIBLE

            // --- Logic: Swap Adapter ---
            binding.rvTasks.adapter = dailyAdapter

            // --- Data: Load Dummy Daily Data ---
            loadDummyDailyData()
        }
    }

//    private fun setupAddTaskButton() {
//        binding.btnAddTask.setOnClickListener {
//            val dialog = BottomSheetDialog(requireContext())
//            val sheetBinding = BottomSheetAddTaskBinding.inflate(layoutInflater)
//            dialog.setContentView(sheetBinding.root)
//
//            sheetBinding.btnSaveTask.setOnClickListener {
//                Toast.makeText(context, "Task Saved!", Toast.LENGTH_SHORT).show()
//                dialog.dismiss()
//            }
//            dialog.show()
//        }
//    }

    // --- DỮ LIỆU GIẢ LẬP (XÓA KHI ĐÃ CÓ ROOM DATABASE) ---
    private fun loadDummyPriorityData() {
        val list = listOf(
            Task(title = "UI Design", description = "Design interface for mobile app", priority = 2, userId = 1, startDate = System.currentTimeMillis(), endDate = System.currentTimeMillis() + 86400000),
            Task(title = "Team Meeting", description = "Discuss project roadmap", priority = 2, userId = 1, startDate = System.currentTimeMillis(), endDate = System.currentTimeMillis() + 100000000),
            Task(title = "Client Feedback", description = "Review feedback from client", priority = 2, userId = 1, startDate = System.currentTimeMillis(), endDate = System.currentTimeMillis() + 200000000)
        )
        priorityAdapter.submitList(list)
    }

    private fun loadDummyDailyData() {
        val list = listOf(
            Task(title = "Morning Workout", isCompleted = true, priority = 1, userId = 1),
            Task(title = "Read 10 pages", isCompleted = false, priority = 1, userId = 1),
            Task(title = "Drink water", isCompleted = false, priority = 1, userId = 1),
            Task(title = "Check emails", isCompleted = false, priority = 1, userId = 1)
        )
        dailyAdapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}