package com.example.todoapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.databinding.ItemCalendarPriorityTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarTaskAdapter(
    private val onTaskClick: (TaskPopulated) -> Unit
) : ListAdapter<TaskPopulated, CalendarTaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    private val dateFormat = SimpleDateFormat("MMM, dd", Locale.ENGLISH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemCalendarPriorityTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemCalendarPriorityTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TaskPopulated) {
            val task = item.task

            binding.tvTitle.text = task.title
            binding.tvDescription.text = task.description ?: "No description"

            if (item.category?.icon != null && item.category?.icon != 0) {
                binding.ivIcon.setImageResource(item.category?.icon!!)
            } else {
                binding.ivIcon.setImageResource(R.drawable.ic_work)
            }

            val startStr = if (task.startDate != null && task.startDate > 0)
                dateFormat.format(Date(task.startDate)) else "Now"
            val endStr = if (task.endDate != null && task.endDate > 0)
                dateFormat.format(Date(task.endDate)) else "..."
            binding.tvDateRange.text = "$startStr - $endStr"


            val indicatorColor = if (task.priority == 2) "#F44336" else "#2196F3"
            binding.viewIndicator.setBackgroundColor(Color.parseColor(indicatorColor))
            binding.tvTitle.setTextColor(Color.parseColor(indicatorColor))

            binding.ivIcon.setColorFilter(Color.parseColor(indicatorColor))

            binding.root.setOnClickListener { onTaskClick(item) }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskPopulated>() {
        override fun areItemsTheSame(oldItem: TaskPopulated, newItem: TaskPopulated) =
            oldItem.task.taskId == newItem.task.taskId

        override fun areContentsTheSame(oldItem: TaskPopulated, newItem: TaskPopulated) =
            oldItem == newItem
    }
}