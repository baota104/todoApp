package com.example.todoapp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.entity.Category
import com.example.todoapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onCategorySelected: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, isSelected: Boolean) {
            binding.tvName.text = category.name
            binding.imgIcon.setImageResource(category.icon)
//            binding.cardContainer.setBackgroundColor(category.colorCode!!.toInt())
            if (isSelected) {
                binding.root.setCardBackgroundColor(Color.parseColor("#2196F3"))
                binding.tvName.setTextColor(Color.WHITE)
            } else {
                binding.root.setCardBackgroundColor(Color.WHITE) // Màu trắng (Normal)
                binding.tvName.setTextColor(Color.BLACK)
            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onCategorySelected(category)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.categoryId == newItem.categoryId
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}