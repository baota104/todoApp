package com.example.todoapp.ui.home.mainn.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.databinding.FragmentEditBinding
import com.example.todoapp.ui.adapter.CategoryAdapter
import com.example.todoapp.ui.adapter.SubTaskCreateAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val arg: EditFragmentArgs by navArgs()
    private lateinit var subTaskAdapter: SubTaskCreateAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var userid : Int = -1;

    private var selectedStartDate : Long = 0L
    private var selectedEndDate : Long = 0L
    private var selectedCategoryId : Int = 0
    private val currentSubTasks = mutableListOf<SubTask>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


    private val editViewModel : EditViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repo = TaskRepository(db.taskDao(), db.subTaskDao())
        val categoryRepository = CategoryRepository(db.categoryDao())
        EditViewModel.Factory(repo,categoryRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userPreferences = UserPreferences(requireContext())
        userid = userPreferences.getUserId()!!

        setArg()
        setupUI()
        setupAdapters()
        setupObserve()

    }
    private fun setupAdapters() {
        // 1. Setup SubTask Adapter (Cho phép xóa item khi click dấu X)
        subTaskAdapter = SubTaskCreateAdapter { subtask ->
            for ( i in currentSubTasks.indices){
                if(currentSubTasks[i].subId == subtask.subId){
                    currentSubTasks.removeAt(i)
                    break
                }
            }
            subTaskAdapter.submitList(currentSubTasks.toList())
        }
        binding.rvSubTasks.layoutManager = LinearLayoutManager(context)
        binding.rvSubTasks.adapter = subTaskAdapter

        categoryAdapter = CategoryAdapter { category ->
            selectedCategoryId = category.categoryId
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = categoryAdapter
    }
    private fun setArg(){
       val taskId = arg.taskid
        if(taskId != -1) {
            editViewModel.setTaskId(taskId)
        }
        else{
            Toast.makeText(context, "Error: Task not found", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

    }
    private fun setupUI(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

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
                // Tạo Subtask mới (subId = 0 để Room biết là thêm mới)
                val newSubTask = SubTask(subId = 0, taskId = 0, content = content, isDone = false)
                currentSubTasks.add(newSubTask)
                subTaskAdapter.submitList(currentSubTasks.toList())
                binding.etSubTaskInput.setText("") // Xóa ô nhập
            }
        }

        binding.btnDeleteTask.setOnClickListener {
            val task = editViewModel.taskDetail.value?.task
            if (task != null) {
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete") { _, _ ->
                        editViewModel.deleteTask(task)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
        binding.btnSave.setOnClickListener {
            saveTask()
        }


    }
    private fun saveTask() {
        val currentTask = editViewModel.taskDetail.value?.task ?: return

        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val priority = if (binding.rgPriority.checkedRadioButtonId == R.id.rbHigh) 2 else 1

        if (title.isEmpty()) {
            Toast.makeText(context, "Please enter title", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTask = currentTask.copy(
            title = title,
            description = description,
            startDate = selectedStartDate,
            endDate = selectedEndDate,
            priority = priority,
            catId = selectedCategoryId

        )

        editViewModel.saveChanges(updatedTask, currentSubTasks)
    }
    private fun setupObserve(){

        editViewModel.resultupdate.observe(viewLifecycleOwner){
            result ->
            val isSuccess = result.first
            val message = result.second
            if(isSuccess){
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            else{
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
            }

        }
        editViewModel.resultdelete.observe(viewLifecycleOwner){
            result ->
            val isSuccess = result.first
            val message = result.second
            if(isSuccess){
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            else{
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
            }
        }
        editViewModel.taskDetail.observe(viewLifecycleOwner) { taskPopulated ->
            bind(taskPopulated)
        }
        editViewModel.categories(userid).observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }
    }

    private fun bind(data: TaskPopulated){
        val task = data.task

        binding.etTitle.setText(task.title)
        binding.etDescription.setText(task.description)

        selectedStartDate = task.startDate ?: System.currentTimeMillis()
        selectedEndDate = task.endDate ?: System.currentTimeMillis()

        binding.tvStartDate.text = dateFormat.format(selectedStartDate)
        binding.tvEndDate.text = dateFormat.format(selectedEndDate)

        if (task.priority == 2) {
            binding.rgPriority.check(R.id.rbHigh)
        } else {
            binding.rgPriority.check(R.id.rbLow)
        }

        if (data.category != null) {
            selectedCategoryId = data.category.categoryId
        }

        currentSubTasks.clear()
        currentSubTasks.addAll(data.subTasks)
        subTaskAdapter.submitList(currentSubTasks.toList())

    }
    private fun showDatePicker(onDateSelected: (String, Long) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(year, month, day)
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            onDateSelected(format.format(calendar.time), calendar.timeInMillis)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}