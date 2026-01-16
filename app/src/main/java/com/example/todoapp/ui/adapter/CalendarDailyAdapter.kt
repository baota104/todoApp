package com.example.todoapp.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entity.Task
import com.example.todoapp.databinding.ItemDailyTaskBinding

class CalendarDailyAdapter(
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<CalendarDailyAdapter.ViewHolder>() {

    private val tasks = mutableListOf<Task>()

    fun submitList(list: List<Task>) {
        tasks.clear()
        tasks.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Sử dụng lại layout item_daily_task.xml
        val binding = ItemDailyTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size

    inner class ViewHolder(private val binding: ItemDailyTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvDailyTitle.text = task.title

            // Xử lý Checkbox (chỉ hiển thị, chưa lưu DB ở bước này)
            binding.rbStatus.isChecked = task.isCompleted

            binding.root.setOnClickListener { onTaskClick(task) }

            binding.rbStatus.setOnClickListener {
                // Logic update status task ở đây (sẽ làm sau)
                task.isCompleted = !task.isCompleted
            }
        }
    }
}