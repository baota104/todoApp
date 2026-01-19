package com.example.todoapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.entity.TaskWithCategory // Import class mới
import com.example.todoapp.databinding.ItemCalendarPriorityTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarTaskAdapter(
    private val onTaskClick: (TaskWithCategory) -> Unit
) : ListAdapter<TaskWithCategory, CalendarTaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

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

        fun bind(item: TaskWithCategory) {
            val task = item.task // Lấy đối tượng Task thực sự ra

            binding.tvTitle.text = task.title
            binding.tvDescription.text = task.description ?: "No description"

            // --- XỬ LÝ ICON ---
            if (item.categoryIcon != null && item.categoryIcon != 0) {
                // Nếu có icon từ Category -> Hiển thị
                binding.ivIcon.setImageResource(item.categoryIcon)
            } else {
                // Nếu không có (hoặc category bị xóa) -> Hiển thị icon mặc định
                binding.ivIcon.setImageResource(R.drawable.ic_work) // Ví dụ icon mặc định
            }

            // (Optional) Nếu muốn tint màu icon theo màu Category luôn
            /* if (item.categoryColor != null) {
                binding.ivIcon.setColorFilter(item.categoryColor)
            }
            */

            // --- XỬ LÝ NGÀY THÁNG (Giữ nguyên) ---
            val startStr = if (task.startDate != null && task.startDate > 0)
                dateFormat.format(Date(task.startDate)) else "Now"
            val endStr = if (task.endDate != null && task.endDate > 0)
                dateFormat.format(Date(task.endDate)) else "..."
            binding.tvDateRange.text = "$startStr - $endStr"

            // --- XỬ LÝ MÀU INDICATOR (Giữ nguyên) ---
            val indicatorColor = if (task.priority == 2) "#F44336" else "#2196F3"
            binding.viewIndicator.setBackgroundColor(Color.parseColor(indicatorColor))
            binding.tvTitle.setTextColor(Color.parseColor(indicatorColor))

            // Lưu ý: Nếu muốn tint icon theo Priority (như bài trước) thì dùng dòng dưới.
            // Còn muốn hiển thị đúng màu gốc của icon Category thì bỏ dòng dưới đi.
            binding.ivIcon.setColorFilter(Color.parseColor(indicatorColor))

            binding.root.setOnClickListener { onTaskClick(item) }
        }
    }

    // Cập nhật DiffCallback
    class TaskDiffCallback : DiffUtil.ItemCallback<TaskWithCategory>() {
        override fun areItemsTheSame(oldItem: TaskWithCategory, newItem: TaskWithCategory) =
            oldItem.task.taskId == newItem.task.taskId

        override fun areContentsTheSame(oldItem: TaskWithCategory, newItem: TaskWithCategory) =
            oldItem == newItem
    }
}