package com.example.todoapp.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entity.Task
import com.example.todoapp.databinding.ItemCalendarPriorityTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarPriorityAdapter(
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<CalendarPriorityAdapter.ViewHolder>() {

    private val tasks = mutableListOf<Task>()

    fun submitList(list: List<Task>) {
        tasks.clear()
        tasks.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalendarPriorityTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size

    inner class ViewHolder(private val binding: ItemCalendarPriorityTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTitle.text = task.title
            binding.tvDescription.text = task.description ?: ""

            // Format ngày: "Feb, 21 - Mar, 12"
            val dateFormat = SimpleDateFormat("MMM, dd", Locale.ENGLISH)

            val startStr = if (task.startDate!! > 0) dateFormat.format(Date(task.startDate)) else "Now"
            val endStr = if (task.endDate!! > 0) dateFormat.format(Date(task.endDate)) else "TBD"

            binding.tvDateRange.text = "$startStr - $endStr"

            binding.root.setOnClickListener { onTaskClick(task) }
        }
    }
}