package com.example.todoapp.ui.home.calendar.calendarfrag

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.databinding.FragmentCalendarBinding
import com.example.todoapp.ui.adapter.CalendarTaskAdapter
import com.example.todoapp.ui.calendar.CalendarAdapter
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by viewModels {
        val context = requireContext()
        val db = AppDatabase.getDatabase(context)
        val userPrefs = UserPreferences(context)
        val taskrepo = TaskRepository(db.taskDao(),db.subTaskDao())
        CalendarViewModel.Factory(taskrepo,userPrefs)
    }

    private lateinit var taskAdapter: CalendarTaskAdapter
    private lateinit var calendarStripAdapter: CalendarAdapter

    private var fullTaskList: List<TaskPopulated> = emptyList()
    private var isPriorityTabSelected = true
    private var selectedDate: Date = Calendar.getInstance().time

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCalendarBinding.bind(view)
        setupAdapters()
        setupCalendarStrip()
        setupTabs()
        setupActions()
        observeData()
        setupHeaderDate()
    }

    private fun setupHeaderDate() {
        val format = java.text.SimpleDateFormat("MMM, yyyy", Locale.ENGLISH)
        binding.tvCurrentMonth.text = format.format(Date())
    }

    private fun setupAdapters() {
        taskAdapter = CalendarTaskAdapter { task ->
            // detail
            Toast.makeText(context, "Clicked: ${task.task.title}", Toast.LENGTH_SHORT).show()
        }

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun setupCalendarStrip() {
        calendarStripAdapter = CalendarAdapter { date ->
            selectedDate = date
            Toast.makeText(context, "Filter by: $date", Toast.LENGTH_SHORT).show()
            updateListByFilter()
        }

        binding.rvCalendarStrip.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = calendarStripAdapter
        }

        val calendar = Calendar.getInstance()
        val dateList = mutableListOf<Date>()
        for (i in 0..30) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        calendarStripAdapter.submitList(dateList)
    }

    private fun setupTabs() {
        binding.tvTabPriority.setOnClickListener {
            if (!isPriorityTabSelected) switchTab(true)
        }

        binding.tvTabDaily.setOnClickListener {
            if (isPriorityTabSelected) switchTab(false)
        }
    }

    private fun setupActions() {
        binding.btnAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_addTaskFragment)
        }
    }
    private fun observeData() {
      calendarViewModel.taskList.observe(viewLifecycleOwner){tasks->
          fullTaskList = tasks
          updateListByFilter()
      }

    }

    private fun switchTab(isPriority: Boolean) {
        isPriorityTabSelected = isPriority
        val context = requireContext()

        if (isPriority) {
            // UI: Chọn Priority
            binding.tvTabPriority.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.primary_blue
                )
            )
            binding.indicatorPriority.visibility = View.VISIBLE

            binding.tvTabDaily.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            binding.indicatorDaily.visibility = View.INVISIBLE
        } else {
            // UI: Chọn Daily
            binding.tvTabPriority.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            binding.indicatorPriority.visibility = View.INVISIBLE

            binding.tvTabDaily.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            binding.indicatorDaily.visibility = View.VISIBLE
        }

        updateListByFilter()
    }

    // loc du lieu
    private fun updateListByFilter() {
        val targetPriority = if (isPriorityTabSelected) 2 else 1
        val selectedDateStartOfDay = getStartOfDay(selectedDate.time)

        val filteredList = fullTaskList.filter { item ->
            val task = item.task

            val matchPriority = task.priority == targetPriority
            var matchDate = false

            if (task.startDate != null && task.startDate > 0) {
                val taskStart = getStartOfDay(task.startDate)

                if (task.endDate != null && task.endDate > 0) {
                    val taskEnd = getStartOfDay(task.endDate)

                    matchDate = selectedDateStartOfDay in taskStart..taskEnd
                } else {
                    matchDate = selectedDateStartOfDay == taskStart
                }
            }
            matchPriority && matchDate
        }
        taskAdapter.submitList(filteredList)

    }

    private fun getStartOfDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}