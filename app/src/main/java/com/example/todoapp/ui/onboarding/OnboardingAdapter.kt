package com.example.todoapp.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageRes: Int
)

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val img: ImageView = view.findViewById(R.id.imgIllustration)
        private val title: TextView = view.findViewById(R.id.tvTitle)
        private val desc: TextView = view.findViewById(R.id.tvDescription)

        fun bind(item: OnboardingItem) {
            title.text = item.title
            desc.text = item.description
            img.setImageResource(item.imageRes) // Nhớ thêm ảnh vào res/drawable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}