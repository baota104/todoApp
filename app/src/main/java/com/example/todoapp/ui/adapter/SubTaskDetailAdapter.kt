package com.example.todoapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.databinding.ItemSubtaskDetailBinding

class SubTaskDetailAdapter(
    private val onSubtaskChecked : (SubTask,Boolean) ->Unit
):ListAdapter<SubTask,SubTaskDetailAdapter.ViewHolder>(SubTaskDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSubtaskDetailBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding:ItemSubtaskDetailBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:SubTask){
                binding.tvDailyTitle.text = item.content

            binding.rbStatus.setOnCheckedChangeListener(null)
            binding.rbStatus.setOnClickListener(null)

            binding.rbStatus.isChecked = item.isDone

            binding.rbStatus.setOnClickListener {
                val newState = binding.rbStatus.isChecked
                onSubtaskChecked(item, newState)
            }
        }
    }
    class SubTaskDiffCallback : DiffUtil.ItemCallback<SubTask>() {
        override fun areItemsTheSame(oldItem: SubTask, newItem: SubTask) = oldItem.subId == newItem.subId
        override fun areContentsTheSame(oldItem: SubTask, newItem: SubTask) = oldItem == newItem
    }
}