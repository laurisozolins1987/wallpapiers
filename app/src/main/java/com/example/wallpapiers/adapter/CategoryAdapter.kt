package com.example.wallpapiers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wallpapiers.R
import com.example.wallpapiers.databinding.ItemCategoryBinding
import com.example.wallpapiers.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedCategory: String = categories.firstOrNull()?.name.orEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    fun updateSelection(category: String) {
        selectedCategory = category
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            val isSelected = category.name == selectedCategory

            binding.tvCategoryName.text = category.name
            Glide.with(binding.ivCategory)
                .load(category.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .centerCrop()
                .into(binding.ivCategory)

            binding.cardCategory.strokeWidth = if (isSelected) 3 else 0
            binding.cardCategory.strokeColor = ContextCompat.getColor(
                binding.root.context,
                if (isSelected) R.color.accent else android.R.color.transparent
            )
            binding.tvCategoryName.alpha = if (isSelected) 1f else 0.82f
            binding.selectionOverlay.alpha = if (isSelected) 0.18f else 0.32f

            binding.root.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }
}
