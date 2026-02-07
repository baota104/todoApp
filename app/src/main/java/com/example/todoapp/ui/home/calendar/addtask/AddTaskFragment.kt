package com.example.todoapp.ui.home.calendar.addtask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.SubTaskRepository
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.databinding.FragmentAddTaskBinding
import com.example.todoapp.ui.adapter.CategoryAdapter
import com.example.todoapp.ui.adapter.SubTaskCreateAdapter
import com.example.todoapp.ui.home.calendar.bottomcategory.AddCategoryBottomSheet
import com.example.todoapp.ui.home.calendar.bottomcategory.CategoryViewModel
import com.example.todoapp.utils.AlarmScheduler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTaskFragment : Fragment(R.layout.fragment_add_task) {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!


    private val taskViewModel: TaskViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val userPreferences = UserPreferences(requireContext())
        val taskRepository = TaskRepository(db.taskDao(),db.subTaskDao())
        val subTaskRepository = SubTaskRepository(db.subTaskDao())
        val categoryRepository = CategoryRepository(db.categoryDao())

        TaskViewModel.Factory(taskRepository, subTaskRepository,categoryRepository,userPreferences,context = requireContext().applicationContext)
    }


    private var selectedStartDate: Long? = null
    private var selectedEndDate: Long? = null
    private var selectedCategoryId: Int? = null
    private var selectedPriority: Int = 1;

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subTaskAdapter: SubTaskCreateAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTaskBinding.bind(view)
        setupAdapters()
        setupInputs()
        observeViewModel()
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { category ->
            android.util.Log.d("DEBUG_CATEGORY", "Đã chọn category: ${category.categoryId} - ${category.name}")
            selectedCategoryId = category.categoryId
        }
        binding.rvCategories.adapter = categoryAdapter

        subTaskAdapter = SubTaskCreateAdapter { subTask ->
            taskViewModel.removeTempSubTask(subTask)
        }
        binding.rvSubTasks.adapter = subTaskAdapter
    }

    private fun setupInputs() {

        binding.tvStartDate.setOnClickListener {
            showDateTimePicker { date, millis ->
                binding.tvStartDate.text = date
                selectedStartDate = millis
            }
        }

        binding.tvEndDate.setOnClickListener {
            showDateTimePicker { date, millis ->
                binding.tvEndDate.text = date
                selectedEndDate = millis
            }
        }

        binding.btnAddSubItem.setOnClickListener {
            val content = binding.etSubTaskInput.text.toString().trim()
            if (content.isNotEmpty()) {
                taskViewModel.addTempSubTask(content)
                binding.etSubTaskInput.setText("")
            }
        }

        binding.btnAddCategory.setOnClickListener {
            val bottomSheet = AddCategoryBottomSheet()
            bottomSheet.show(parentFragmentManager, "AddCategoryBottomSheet")
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.rgPriority.setOnCheckedChangeListener { _, checkedId ->
            selectedPriority = if (checkedId == R.id.rbHigh) 2 else 1
        }

        binding.btnCreateTask.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()

            if (selectedStartDate == null) {
                Toast.makeText(context, "Please select a start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedEndDate == null) {
                Toast.makeText(context, "Please select an end date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedEndDate!! < selectedStartDate!!) {
                Toast.makeText(context, "End date must be after start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (title.isEmpty()) {
                Toast.makeText(context, "Title is required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedCategoryId == null) {
                Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            taskViewModel.createTask(
                title = title,
                desc = desc,
                startDate = selectedStartDate,
                endDate = selectedEndDate,
                categoryId = selectedCategoryId,
                priority = selectedPriority
            )
        }
    }

    private fun observeViewModel() {

        taskViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }


        taskViewModel.tempSubTasks.observe(viewLifecycleOwner) { subTasks ->
            subTaskAdapter.submitList(subTasks)
        }


        lifecycleScope.launch {
            taskViewModel.taskEvent.collect { event ->
                if (event == "Success") {
                    Toast.makeText(context, "Task created successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, event, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDateTimePicker(onDateTimeSelected: (String, Long) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // chon ngay
        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            // chon gio
            TimePickerDialog(requireContext(), { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

                onDateTimeSelected(format.format(calendar.time), calendar.timeInMillis)

            }, currentHour, currentMinute, true).show() // true = Định dạng 24h

        }, currentYear, currentMonth, currentDay).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}