package com.example.todoapp.ui.home.mainn.taskdetail

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.repository.SubTaskRepository
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.databinding.FragmentTaskDetailBinding
import com.example.todoapp.ui.adapter.SubTaskDetailAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TaskDetailFragment : Fragment() {

    private var _binding : FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private val args: TaskDetailFragmentArgs by navArgs()
    private lateinit var subTaskAdapter: SubTaskDetailAdapter
    private val taskDetailViewModel:TaskDetailViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val taskRepository = TaskRepository(db.taskDao(),db.subTaskDao())
        TaskDetailViewModel.Factory(taskRepository)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObserver()
        handleArguments()

    }

    private fun setupUI(){
        binding.btnClose.setOnClickListener{
            findNavController().popBackStack()
        }

        subTaskAdapter = SubTaskDetailAdapter{
            Subtask,ischeck ->
            handleSubTaskClick(Subtask,ischeck)
        }
        binding.rvSubTasksDetail.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = subTaskAdapter
            isNestedScrollingEnabled = false
        }

        binding.btnfinish.setOnClickListener {
            val currentdata = taskDetailViewModel.taskDetail.value ?: return@setOnClickListener
            dialogconfirmfinishtask(currentdata.task.taskId)
        }
        binding.btnedit.setOnClickListener {
            val currentdata = taskDetailViewModel.taskDetail.value ?: return@setOnClickListener
            val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToEditFragment(currentdata.task.taskId)
            findNavController().navigate(action)
        }

    }
    private fun dialogconfirmfinishtask(taskid:Int){
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirm")
            .setMessage("Are you sure you want to finish this task?")
            .setPositiveButton("Yes"){dialog, _ ->
                taskDetailViewModel.finishtask(taskid)
                dialog.dismiss()
            }
            .setNegativeButton("No"){dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun handleArguments() {
        val receivedTaskId = args.taskId
        if (receivedTaskId != -1) {
            taskDetailViewModel.setTaskId(receivedTaskId)
        } else {
            Toast.makeText(context, "Error: Task not found", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setupObserver() {
        taskDetailViewModel.taskDetail.observe(viewLifecycleOwner) { taskPopulated ->
            if (taskPopulated != null) {
                bindData(taskPopulated)
            }
        }
        taskDetailViewModel.updateResult.observe(viewLifecycleOwner){
            result ->
            val isSuccess = result.first
            val message = result.second
            if(isSuccess){
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun bindData(data: TaskPopulated) {
        val task = data.task
        val category = data.category
        val subTasks = data.subTasks

        binding.tvDetailTitle.text = task.title
        binding.tvDescription.text = task.description ?: "No description"

//        val defaultColor = ContextCompat.getColor(requireContext(), R.color.primary_blue)
//        val colorInt = category?.colorCode ?: defaultColor
//
//        binding.tvDetailTitle.setTextColor(colorInt)
        binding.ivIcon.setImageResource(category?.icon ?: R.drawable.ic_work)

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
        binding.tvStartDate.text = if (task.startDate != null) dateFormat.format(Date(task.startDate)) else "N/A"
        binding.tvEndDate.text = if (task.endDate != null) dateFormat.format(Date(task.endDate)) else "N/A"

        if (task.endDate != null) {
            setupCountdownTimer(task.endDate)
        } else {
            binding.layoutTimer.visibility = View.GONE
        }

        calculateProgress(data)

        subTaskAdapter.submitList(subTasks)
    }

    private fun setupCountdownTimer(endDate: Long) {
        val currentTime = System.currentTimeMillis()
        val diff = endDate - currentTime

        if (diff <= 0) {
            binding.layoutTimer.visibility = View.GONE
            binding.tvOverdueMessage.visibility = View.VISIBLE
            return
        }

        binding.tvOverdueMessage.visibility = View.GONE
        binding.layoutTimer.visibility = View.VISIBLE

        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

        if (days > 0) {
            binding.cardTimer3.isVisible = true
            binding.layoutTimer.weightSum = 3f

            binding.tvTime1.text = days.toString()
            binding.tvLabel1.text = "days"

            binding.tvTime2.text = hours.toString()
            binding.tvLabel2.text = "hours"

            binding.tvTime3.text = minutes.toString()
            binding.tvLabel3.text = "minutes"
        } else {
            binding.cardTimer3.isVisible = false
            binding.layoutTimer.weightSum = 2f

            binding.tvTime1.text = hours.toString()
            binding.tvLabel1.text = "hours"

            binding.tvTime2.text = minutes.toString()
            binding.tvLabel2.text = "minutes"
        }
    }

    private fun calculateProgress(data: TaskPopulated) {
        val subTasks = data.subTasks

        val progress = if (subTasks.isNotEmpty()) {
            val completedCount = subTasks.count { it.isDone }
            (completedCount * 100) / subTasks.size
        } else {
            if (data.task.isCompleted) 100 else 0
        }
        binding.progressBarDetail.progress = progress
        binding.tvProgressPercent.text = "$progress%"
    }
    private fun handleSubTaskClick(subTask: SubTask,isChecked: Boolean){
    val currentdata = taskDetailViewModel.taskDetail.value ?: return
    val subTaskList = currentdata.subTasks
    if(isChecked){
        val hasUnfinishedTasks = subTaskList.any { it.subId != subTask.subId && !it.isDone }

        if (!hasUnfinishedTasks) {
            // Nếu KHÔNG còn việc nào chưa xong -> Đây chính là việc cuối cùng!
            showFinishParentTaskDialog(subTask)
            return
        }
    }
        showConfirmationDialog(subTask,isChecked)
    }
    private fun showFinishParentTaskDialog(subTask: SubTask){
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Congratulation !")
            .setMessage("You have finished all subtasks of this task.Do you want to finish this task?")
            .setPositiveButton("Yes, finish it") { dialog, _ ->
                taskDetailViewModel.updateSubTaskStatus(subTask, true)

                taskDetailViewModel.updateParentTaskStatus(true)

                dialog.dismiss()
            }
            .setNegativeButton("Không, chỉ việc nhỏ") { dialog, _ ->
            taskDetailViewModel.updateSubTaskStatus(subTask, true)
            dialog.dismiss()
        }
            .setNeutralButton("Hủy") { dialog, _ ->
                subTaskAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setOnCancelListener {
                subTaskAdapter.notifyDataSetChanged()
            }
            .show()
    }

    private fun showConfirmationDialog(subTask: SubTask, isChecked: Boolean){
        val title = "Confirm"
        val message = if(isChecked){
            "Are you sure you want to mark this subtask as done?"
        }
        else{
            "Are you sure you want to mark this subtask as undone?"
        }
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes"){dialog, _ ->
                taskDetailViewModel.updateSubTaskStatus(subTask, isChecked)
                dialog.dismiss()
            }
            .setNegativeButton("No"){dialog, _ ->
                dialog.dismiss()
                subTaskAdapter.notifyDataSetChanged()
            }
            .setOnCancelListener {
                subTaskAdapter.notifyDataSetChanged()
            }
            .show()

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}