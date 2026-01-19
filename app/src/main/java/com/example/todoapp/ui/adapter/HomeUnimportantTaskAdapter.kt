package com.example.todoapp.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entity.TaskWithCategory
import com.example.todoapp.databinding.ItemDailyTaskBinding

class HomeUnimportantTaskAdapter(
    private val onTaskClick: (TaskWithCategory) -> Unit,
    private val onTaskStatusChanged: (TaskWithCategory, Boolean) -> Unit // Callback check/uncheck
) : ListAdapter<TaskWithCategory, HomeUnimportantTaskAdapter.DailyViewHolder>(TaskDiffCallback()) {

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

        fun bind(item: TaskWithCategory) {
            val task = item.task

            binding.tvDailyTitle.text = task.title

            // 1. Xử lý trạng thái Check (RadioButton)
            // Gỡ listener trước khi set trạng thái để tránh trigger vô tận
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

            // 3. Sự kiện Click vào RadioButton (Update Status)
            binding.rbStatus.setOnClickListener {
                // Đảo ngược trạng thái hiện tại
                val newStatus = !task.isCompleted
                onTaskStatusChanged(item, newStatus)
            }

            // 4. Sự kiện Click vào toàn bộ Card (Mở Detail)
            binding.root.setOnClickListener { onTaskClick(item) }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskWithCategory>() {
        override fun areItemsTheSame(oldItem: TaskWithCategory, newItem: TaskWithCategory) =
            oldItem.task.taskId == newItem.task.taskId
        override fun areContentsTheSame(oldItem: TaskWithCategory, newItem: TaskWithCategory) =
            oldItem == newItem
    }
}