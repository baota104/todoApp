package com.example.todoapp.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.ItemCalendarDateBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(

    private val onDateClick : (Date)->Unit
) : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {

    private val dates = mutableListOf<Date>()
    private var selectedPosition = 0

    fun submitList(list: List<Date>) {
        dates.clear()
        dates.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemCalendarDateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position], position == selectedPosition)
    }

    override fun getItemCount() = dates.size

    inner class DateViewHolder(private val binding: ItemCalendarDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: Date, isSelected: Boolean) {
            val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
            val dateFormat = SimpleDateFormat("dd", Locale.ENGLISH)

            binding.tvDayName.text = dayFormat.format(date)
            binding.tvDayNumber.text = dateFormat.format(date)

            // Xử lý trạng thái chọn
            binding.itemContainer.isSelected = isSelected

            binding.root.setOnClickListener {
                val previous = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onDateClick(date)
            }
        }
    }
}