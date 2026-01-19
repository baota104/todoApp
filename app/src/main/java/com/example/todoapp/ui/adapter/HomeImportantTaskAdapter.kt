package com.example.todoapp.ui.home.mainn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.entity.TaskWithCategory
import com.example.todoapp.databinding.ItemPriorityTaskBinding // Tên file XML của bạn
import java.util.*
import java.util.concurrent.TimeUnit

class HomeImportantTaskAdapter(
    private val onTaskClick: (TaskWithCategory) -> Unit
) : ListAdapter<TaskWithCategory, HomeImportantTaskAdapter.PriorityViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriorityViewHolder {
        val binding = ItemPriorityTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PriorityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PriorityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PriorityViewHolder(private val binding: ItemPriorityTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TaskWithCategory) {
            val task = item.task

            // 1. Title
            binding.tvTaskTitle.text = task.title

            // 2. Tính toán Days Left
            if (task.endDate != null && task.endDate > 0) {
                val diffInMillis = task.endDate - System.currentTimeMillis()
                val daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                if (daysLeft < 0) {
                    binding.tvDaysLeft.text = "Overdue"
                    binding.tvDaysLeft.setBackgroundResource(R.drawable.bg_circle_color) // Bạn cần tạo drawable này nếu muốn đổi màu đỏ
                } else if (daysLeft == 0L) {
                    binding.tvDaysLeft.text = "Today"
                } else {
                    binding.tvDaysLeft.text = "$daysLeft days"
                }
            } else {
                binding.tvDaysLeft.text = "No deadline"
            }

            // 3. Progress Bar & Percent
            // Hiện tại Database chưa có cột progress, tạm thời mình giả lập logic:
            // Nếu completed = 100%, chưa = 50% (Hoặc sau này tính theo SubTask)
            val progress = if (task.isCompleted) 100 else 0 // Hoặc lấy từ task.progress nếu bạn đã thêm cột này

            binding.progressBar.progress = progress
            binding.tvPercent.text = "$progress%"

            // 4. Click Event
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