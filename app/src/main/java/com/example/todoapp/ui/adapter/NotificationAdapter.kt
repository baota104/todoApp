package com.example.todoapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.entity.Notification
import com.example.todoapp.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(
    private val onClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.tvTitle.text = notification.title
            binding.tvMessage.text = notification.message

            val sdf = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
            when (notification.type) {
                1 -> { // Statistic
                    binding.ivIcon.setImageResource(R.drawable.statistics) // Cần có icon này
                    binding.ivIcon.setColorFilter(Color.parseColor("#2196F3")) // Xanh
                    binding.cardIcon.setCardBackgroundColor(Color.parseColor("#E3F2FD")) // Nền xanh nhạt
                }
                2 -> { // Completed
                    binding.ivIcon.setImageResource(R.drawable.delete)
                    binding.ivIcon.setColorFilter(Color.parseColor("#4CAF50")) // Xanh lá
                    binding.cardIcon.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
                }
                3 -> { // Warning / Reminder
                    binding.ivIcon.setImageResource(R.drawable.ic_warning)
                    binding.ivIcon.setColorFilter(Color.parseColor("#FF9800")) // Cam
                    binding.cardIcon.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
                }
                else -> {
                    binding.ivIcon.setImageResource(R.drawable.ic_notifications)
                }
            }

            binding.root.setOnClickListener {
                onClick(notification)
            }
        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.notiId == newItem.notiId
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
}