package com.example.wallpapiers.util

import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.model.WallpaperSortOption
import com.example.wallpapiers.prefs.AppPreferences
import kotlin.math.abs

object WallpaperRanking {

    fun score(wallpaper: Wallpaper, preferences: AppPreferences): Int {
        val seededScore = 11_500 +
            abs(wallpaper.id.hashCode() % 7_500) +
            ((wallpaper.width + wallpaper.height) % 2_000)
        val providerBoost = ((wallpaper.downloads ?: 0) / 25) +
            ((wallpaper.views ?: 0) / 300) +
            ((wallpaper.likes ?: 0) * 40)
        return seededScore + providerBoost + preferences.getInteractionBoost(wallpaper.id)
    }

    fun sort(
        wallpapers: List<Wallpaper>,
        preferences: AppPreferences,
        sortOption: WallpaperSortOption = WallpaperSortOption.TRENDING
    ): List<Wallpaper> {
        val distinctWallpapers = wallpapers.distinctBy { it.id }

        return when (sortOption) {
            WallpaperSortOption.TRENDING -> distinctWallpapers.sortedByDescending { score(it, preferences) }
            WallpaperSortOption.POPULAR -> distinctWallpapers.sortedWith(
                compareByDescending<Wallpaper> { popularityScore(it, preferences) }
                    .thenByDescending { score(it, preferences) }
            )
            WallpaperSortOption.CREATOR -> distinctWallpapers.sortedWith(
                compareBy<Wallpaper> { it.photographer.lowercase() }
                    .thenByDescending { score(it, preferences) }
            )
            WallpaperSortOption.TALL -> distinctWallpapers.sortedWith(
                compareByDescending<Wallpaper> { portraitFactor(it) }
                    .thenByDescending { score(it, preferences) }
            )
            WallpaperSortOption.SOURCE -> distinctWallpapers.sortedWith(
                compareBy<Wallpaper> { it.provider.label }
                    .thenByDescending { score(it, preferences) }
            )
        }
    }

    fun formatDownloads(wallpaper: Wallpaper, preferences: AppPreferences): String {
        val baseValue = wallpaper.downloads ?: popularityScore(wallpaper, preferences)
        return "${formatCompactNumber(baseValue)} downloads"
    }

    fun title(wallpaper: Wallpaper): String {
        return wallpaper.title?.takeIf { it.isNotBlank() }
            ?: wallpaper.collection?.takeIf { it.isNotBlank() }
            ?: "Top wallpaper"
    }

    fun subtitle(wallpaper: Wallpaper): String {
        return buildString {
            wallpaper.collection?.takeIf { it.isNotBlank() }?.let {
                append(it)
            }
            if (wallpaper.photographer.isNotBlank()) {
                if (isNotEmpty()) append(" • ")
                append(wallpaper.photographer)
            }
            if (wallpaper.sourceName.isNotBlank()) {
                if (isNotEmpty()) append(" • ")
                append(wallpaper.sourceName)
            }
        }.ifBlank { "Curated wallpaper" }
    }

    fun providerAndCreator(wallpaper: Wallpaper): String {
        return "${wallpaper.photographer} • ${wallpaper.provider.label}"
    }

    private fun popularityScore(wallpaper: Wallpaper, preferences: AppPreferences): Int {
        return ((wallpaper.downloads ?: 0) * 4) +
            ((wallpaper.likes ?: 0) * 35) +
            ((wallpaper.views ?: 0) / 60) +
            preferences.getInteractionBoost(wallpaper.id)
    }

    private fun portraitFactor(wallpaper: Wallpaper): Float {
        return if (wallpaper.width == 0) 0f else wallpaper.height.toFloat() / wallpaper.width.toFloat()
    }

    private fun formatCompactNumber(value: Int): String {
        return when {
            value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000f)
            value >= 1_000 -> String.format("%.1fk", value / 1_000f)
            else -> value.toString()
        }
    }
}
