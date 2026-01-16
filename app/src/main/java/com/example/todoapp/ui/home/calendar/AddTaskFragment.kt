package com.example.todoapp.ui.home.calendar

import android.app.DatePickerDialog
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
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.databinding.FragmentAddTaskBinding
import com.example.todoapp.ui.adapter.CategoryAdapter
import com.example.todoapp.ui.adapter.SubTaskCreateAdapter
import com.example.todoapp.ui.viewmodel.CategoryViewModel
import com.example.todoapp.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTaskFragment : Fragment(R.layout.fragment_add_task) {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!


    private val taskViewModel: TaskViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val userPreferences = UserPreferences(requireContext())
        TaskViewModel.Factory(db.taskDao(), db.subTaskDao(),userPreferences)
    }

    private val categoryViewModel: CategoryViewModel by activityViewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repository = CategoryRepository(db.categoryDao())
        val userPreferences = UserPreferences(requireContext())
        CategoryViewModel.CategoryViewModelFactory(repository,userPreferences)
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
        setupObservers()
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { category ->
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
            showDatePicker { date, millis ->
                binding.tvStartDate.text = date
                selectedStartDate = millis
            }
        }

        binding.tvEndDate.setOnClickListener {
            showDatePicker { date, millis ->
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

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    categoryViewModel.categories.collect { list ->
                        if (list.isEmpty()) {
                            categoryViewModel.createDefaultsIfEmpty()
                        }
                        categoryAdapter.submitList(list)
                    }
                }

                launch {
                    taskViewModel.tempSubTasks.collect { list ->
                        subTaskAdapter.submitList(list)
                    }
                }

                launch {
                    taskViewModel.taskEvent.collect { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (msg.contains("Success")) {
                            findNavController().popBackStack() // Quay về
                        }
                    }
                }
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String, Long) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(year, month, day)
            // Định dạng ngày tháng đẹp (VD: Feb 21, 2024)
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            onDateSelected(format.format(calendar.time), calendar.timeInMillis)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}