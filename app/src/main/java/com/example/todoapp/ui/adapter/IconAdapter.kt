package com.example.todoapp.ui.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.ItemIconSelectionBinding

class IconAdapter(
    private val icons: List<Int>,
    private val onIconSelected: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedPosition = 0

    inner class IconViewHolder(val binding: ItemIconSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(iconRes: Int, isSelected: Boolean) {
            binding.imgIcon.setImageResource(iconRes)

            if (isSelected) {

                binding.containerIcon.setBackgroundResource(R.drawable.bg_circle_blue)
                binding.imgIcon.setColorFilter(Color.WHITE)
            } else {
                binding.containerIcon.setBackgroundResource(R.drawable.bg_circle_gray)
                binding.imgIcon.setColorFilter(Color.WHITE)
            }

            binding.root.setOnClickListener {
                val previous = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onIconSelected(iconRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val binding = ItemIconSelectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return IconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(icons[position], position == selectedPosition)
    }

    override fun getItemCount() = icons.size
}