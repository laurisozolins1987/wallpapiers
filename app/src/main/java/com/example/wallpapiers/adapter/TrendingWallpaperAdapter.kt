package com.example.wallpapiers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.wallpapiers.databinding.ItemTrendingWallpaperBinding
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.prefs.AppPreferences
import com.example.wallpapiers.util.WallpaperImageResolver
import com.example.wallpapiers.util.WallpaperRanking

class TrendingWallpaperAdapter(
    private val onWallpaperClick: (Wallpaper) -> Unit
) : ListAdapter<Wallpaper, TrendingWallpaperAdapter.TrendingWallpaperViewHolder>(DiffCallback()) {

    private var showCredits: Boolean = true
    private var dataSaverEnabled: Boolean = false
    private var cachedPreferences: AppPreferences? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingWallpaperViewHolder {
        val binding = ItemTrendingWallpaperBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrendingWallpaperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingWallpaperViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun applyDisplayPreferences(showCredits: Boolean, dataSaverEnabled: Boolean) {
        this.showCredits = showCredits
        this.dataSaverEnabled = dataSaverEnabled
        notifyDataSetChanged()
    }

    inner class TrendingWallpaperViewHolder(
        private val binding: ItemTrendingWallpaperBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wallpaper: Wallpaper) {
            binding.tvWallpaperTitle.text = WallpaperRanking.title(wallpaper)
            val prefs = cachedPreferences
                ?: AppPreferences(binding.root.context).also { cachedPreferences = it }
            binding.tvMetric.text = WallpaperRanking.formatDownloads(wallpaper, prefs)
            binding.tvPhotographer.text = WallpaperRanking.providerAndCreator(wallpaper)
            binding.tvPhotographer.isVisible = showCredits

            Glide.with(binding.ivWallpaper)
                .load(WallpaperImageResolver.trendingUrl(wallpaper, dataSaverEnabled))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(440, 600)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .into(binding.ivWallpaper)

            binding.root.setOnClickListener {
                onWallpaperClick(wallpaper)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Wallpaper>() {
        override fun areItemsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean = oldItem == newItem
    }
}
