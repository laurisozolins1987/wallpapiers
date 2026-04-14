package com.example.wallpapiers.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpapiers.data.LocalWallpaperCatalog
import com.example.wallpapiers.model.Category
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.model.WallpaperSortOption
import com.example.wallpapiers.prefs.AppPreferences
import com.example.wallpapiers.repository.WallpaperRepository
import com.example.wallpapiers.util.WallpaperRanking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: WallpaperRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _featuredWallpaper = MutableLiveData<Wallpaper?>()
    val featuredWallpaper: LiveData<Wallpaper?> = _featuredWallpaper

    private val _trendingWallpapers = MutableLiveData<List<Wallpaper>>()
    val trendingWallpapers: LiveData<List<Wallpaper>> = _trendingWallpapers

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _totalWallpaperCount = MutableLiveData<Int>()
    val totalWallpaperCount: LiveData<Int> = _totalWallpaperCount

    private var rankedWallpapers: List<Wallpaper> = emptyList()

    init {
        // 1. Categories — instant, no API needed
        _categories.value = repository.getCategories()

        // 2. Local wallpapers — instant, shown immediately
        rankedWallpapers = WallpaperRanking.sort(
            LocalWallpaperCatalog.wallpapers,
            preferences,
            WallpaperSortOption.TRENDING
        )
        publish()

        // 3. API results loaded in background — merges when ready
        loadFromApi()
    }

    fun refreshRankings() {
        if (rankedWallpapers.isEmpty()) return
        rankedWallpapers = WallpaperRanking.sort(rankedWallpapers, preferences, WallpaperSortOption.TRENDING)
        publish()
    }

    private fun loadFromApi() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val feed = withContext(Dispatchers.IO) {
                    repository.getHomeFeed()
                }
                if (feed.isNotEmpty()) {
                    rankedWallpapers = WallpaperRanking.sort(
                        (rankedWallpapers + feed).distinctBy { it.id },
                        preferences,
                        WallpaperSortOption.TRENDING
                    )
                    publish()
                }
            } catch (_: Exception) {
                // Local content already visible — no action needed
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun publish() {
        _featuredWallpaper.postValue(rankedWallpapers.firstOrNull())
        _trendingWallpapers.postValue(rankedWallpapers.drop(1).take(TRENDING_LIMIT))
        _totalWallpaperCount.postValue(rankedWallpapers.size)
    }

    companion object {
        private const val TRENDING_LIMIT = 12
    }
}
