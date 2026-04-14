package com.example.wallpapiers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.wallpapiers.databinding.ItemWallpaperBinding
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.util.WallpaperImageResolver
import com.example.wallpapiers.util.WallpaperRanking

class WallpaperAdapter(private val onWallpaperClick: (Wallpaper) -> Unit) :
    ListAdapter<Wallpaper, WallpaperAdapter.WallpaperViewHolder>(DiffCallback()) {

    private var showCredits: Boolean = true
    private var dataSaverEnabled: Boolean = false
    private var cachedPreferences: com.example.wallpapiers.prefs.AppPreferences? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val binding = ItemWallpaperBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WallpaperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        val wallpaper = getItem(position)
        holder.bind(wallpaper)
    }

    fun applyDisplayPreferences(showCredits: Boolean, dataSaverEnabled: Boolean) {
        this.showCredits = showCredits
        this.dataSaverEnabled = dataSaverEnabled
        notifyDataSetChanged()
    }

    inner class WallpaperViewHolder(private val binding: ItemWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wallpaper: Wallpaper) {
            binding.tvWallpaperTitle.text = WallpaperRanking.title(wallpaper)
            binding.tvPhotographer.text = WallpaperRanking.providerAndCreator(wallpaper)
            val prefs = cachedPreferences
                ?: com.example.wallpapiers.prefs.AppPreferences(binding.root.context).also { cachedPreferences = it }
            binding.tvMetric.text = WallpaperRanking.formatDownloads(wallpaper, prefs)
            binding.tvPhotographer.isVisible = showCredits

            Glide.with(binding.ivWallpaper)
                .load(WallpaperImageResolver.feedUrl(wallpaper, dataSaverEnabled))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(480, 720)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivWallpaper)

            binding.root.setOnClickListener {
                onWallpaperClick(wallpaper)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Wallpaper>() {
        override fun areItemsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean {
            return oldItem == newItem
        }
    }
}
