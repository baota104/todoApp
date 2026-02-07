package com.example.todoapp.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.databinding.ItemDailyTaskBinding

class HomeUnimportantTaskAdapter(
    private val onTaskClick: (TaskPopulated) -> Unit,
    private val onTaskStatusChanged: (TaskPopulated, Boolean) -> Unit // Callback check/uncheck
) : ListAdapter<TaskPopulated, HomeUnimportantTaskAdapter.DailyViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = ItemDailyTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DailyViewHolder(private val binding: ItemDailyTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TaskPopulated) {
            val task = item.task

            binding.tvDailyTitle.text = task.title
            binding.rbStatus.setOnCheckedChangeListener(null)
            binding.rbStatus.isChecked = task.isCompleted

            // 2. Hiệu ứng gạch ngang chữ nếu đã hoàn thành
            if (task.isCompleted) {
                binding.tvDailyTitle.paintFlags = binding.tvDailyTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvDailyTitle.alpha = 0.5f
            } else {
                binding.tvDailyTitle.paintFlags = binding.tvDailyTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.tvDailyTitle.alpha = 1.0f
            }

            binding.rbStatus.setOnClickListener {
                val newStatus = !task.isCompleted
                onTaskStatusChanged(item, newStatus)
            }

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