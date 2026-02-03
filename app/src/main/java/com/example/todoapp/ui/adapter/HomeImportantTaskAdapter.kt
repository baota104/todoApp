package com.example.todoapp.ui.home.mainn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.databinding.ItemPriorityTaskBinding // Tên file XML của bạn
import java.util.concurrent.TimeUnit

class HomeImportantTaskAdapter(
    private val onTaskClick: (TaskPopulated) -> Unit
) : ListAdapter<TaskPopulated, HomeImportantTaskAdapter.PriorityViewHolder>(TaskDiffCallback()) {

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

        fun bind(item: TaskPopulated) {
            val task = item.task
            binding.tvTaskTitle.text = task.title
            binding.ivIcon.setImageResource(item.category?.icon!!)
            val colorResId = item.category.colorCode ?: R.color.card_red
            try {
                val context = binding.root.context
                val layerDrawable = binding.layoutContainer.background?.mutate() as? android.graphics.drawable.LayerDrawable
                val bgShape = layerDrawable?.findDrawableByLayerId(R.id.card_bg_layer) as? android.graphics.drawable.GradientDrawable
                if (bgShape != null) {
                    val colorInt = androidx.core.content.ContextCompat.getColor(context, colorResId)
                    bgShape.setColor(colorInt)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (task.endDate != null && task.endDate > 0) {
                val diffInMillis = task.endDate - System.currentTimeMillis()
                val daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                if (daysLeft < 0) {
                    binding.tvDaysLeft.text = "Overdue"
//                    binding.tvDaysLeft.setBackgroundResource(R.drawable.bg_priority_card_pattern) // Bạn cần tạo drawable này nếu muốn đổi màu đỏ
                } else if (daysLeft == 0L) {
                    binding.tvDaysLeft.text = "Today"
                } else {
                    binding.tvDaysLeft.text = "$daysLeft days"
                }
            } else {
                binding.tvDaysLeft.text = "No deadline"
            }

            if (item.subTasks.isNotEmpty()) {
                val total = item.subTasks.size
                val completed = item.subTasks.count { it.isDone } // Đếm số việc đã xong

                val percent = (completed * 100) / total

                binding.progressBar.progress = percent
                binding.tvPercent.text = "$percent%"
            } else {
                val percent = if (task.isCompleted) 100 else 0
                binding.progressBar.progress = percent
                binding.tvPercent.text = "$percent%"
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