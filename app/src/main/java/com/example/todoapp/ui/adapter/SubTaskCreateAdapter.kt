package com.example.todoapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.databinding.ItemSubtaskBinding

class SubTaskCreateAdapter(
    private val onDeleteClick: (SubTask) -> Unit
) : ListAdapter<SubTask, SubTaskCreateAdapter.SubTaskViewHolder>(SubTaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        // Bạn cần tạo layout item_sub_task_create.xml gồm: 1 TextView + 1 ImageButton (Delete)
        val binding = ItemSubtaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubTaskViewHolder(private val binding: ItemSubtaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subTask: SubTask) {
            binding.tvContent.text = subTask.content
            binding.btnDelete.setOnClickListener {
                onDeleteClick(subTask)
            }
        }
    }

    class SubTaskDiffCallback : DiffUtil.ItemCallback<SubTask>() {
        override fun areItemsTheSame(oldItem: SubTask, newItem: SubTask) = oldItem.content == newItem.content
        override fun areContentsTheSame(oldItem: SubTask, newItem: SubTask) = oldItem == newItem
    }
}