package com.example.wallpapiers.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpapiers.data.LocalWallpaperCatalog
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.model.WallpaperSortOption
import com.example.wallpapiers.model.WallpaperSourceFilter
import com.example.wallpapiers.prefs.AppPreferences
import com.example.wallpapiers.repository.WallpaperRepository
import com.example.wallpapiers.util.WallpaperRanking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val repository: WallpaperRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Wallpaper>>()
    val searchResults: LiveData<List<Wallpaper>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _hasMore = MutableLiveData<Boolean>(false)
    val hasMore: LiveData<Boolean> = _hasMore

    private val _sortOption = MutableLiveData(WallpaperSortOption.TRENDING)
    val sortOption: LiveData<WallpaperSortOption> = _sortOption

    private val _sourceFilter = MutableLiveData(WallpaperSourceFilter.ALL)
    val sourceFilter: LiveData<WallpaperSourceFilter> = _sourceFilter

    private val _screenTitle = MutableLiveData<String>("Search")
    val screenTitle: LiveData<String> = _screenTitle

    private var currentPage = 1
    private var currentMode: SearchMode = SearchMode.Search("")
    private var currentItems = mutableListOf<Wallpaper>()
    private var requestInFlight = false

    fun start(
        initialQuery: String,
        initialTitle: String,
        openAsCategory: Boolean
    ) {
        if (initialQuery.isBlank()) return
        currentMode = if (openAsCategory) SearchMode.Category(initialQuery) else SearchMode.Search(initialQuery)
        _screenTitle.value = initialTitle.ifBlank {
            if (openAsCategory) initialQuery else "Search"
        }
        reload()
    }

    fun searchWallpapers(query: String) {
        currentMode = SearchMode.Search(query)
        _screenTitle.value = "Search"
        reload()
    }

    fun applySort(sortOption: WallpaperSortOption) {
        if (_sortOption.value == sortOption) return
        _sortOption.value = sortOption
        reload()
    }

    fun applySourceFilter(sourceFilter: WallpaperSourceFilter) {
        if (_sourceFilter.value == sourceFilter) return
        _sourceFilter.value = sourceFilter
        reload()
    }

    fun loadMore() {
        if (requestInFlight || _hasMore.value != true) return
        currentPage += 1
        fetchPage(append = true)
    }

    private fun reload() {
        if (!hasActiveTarget()) return
        currentPage = 1
        currentItems.clear()
        fetchPage(append = false)
    }

    private fun fetchPage(append: Boolean) {
        if (requestInFlight || !hasActiveTarget()) return
        requestInFlight = true

        // Show local results IMMEDIATELY for first page so user sees content instantly
        if (!append && currentPage == 1) {
            val localResults = when (val mode = currentMode) {
                is SearchMode.Category -> LocalWallpaperCatalog.wallpapersForCategory(mode.categoryName)
                is SearchMode.Search -> LocalWallpaperCatalog.search(mode.query)
            }
            if (localResults.isNotEmpty()) {
                val sorted = WallpaperRanking.sort(localResults, preferences, _sortOption.value ?: WallpaperSortOption.TRENDING)
                val preview = sorted.take(MAX_LOCAL_PREVIEW)
                currentItems = preview.toMutableList()
                _searchResults.postValue(preview)
            }
        }

        viewModelScope.launch {
            if (append) {
                _isLoadingMore.postValue(true)
            } else {
                _isLoading.postValue(true)
            }
            try {
                val sortOption = _sortOption.value ?: WallpaperSortOption.TRENDING
                val sourceFilter = _sourceFilter.value ?: WallpaperSourceFilter.ALL

                val result = withContext(Dispatchers.IO) {
                    when (val mode = currentMode) {
                        is SearchMode.Search -> repository.searchWallpapers(
                            query = mode.query,
                            page = currentPage,
                            sortOption = sortOption,
                            sourceFilter = sourceFilter
                        )
                        is SearchMode.Category -> repository.getWallpapersByCategory(
                            categoryName = mode.categoryName,
                            page = currentPage,
                            sortOption = sortOption,
                            sourceFilter = sourceFilter
                        )
                    }
                }

                val sortedItems = withContext(Dispatchers.Default) {
                    val items = if (append) {
                        currentItems + result.items
                    } else {
                        result.items.toList()
                    }
                    WallpaperRanking.sort(
                        items,
                        preferences,
                        sortOption
                    )
                }
                currentItems = sortedItems.toMutableList()
                _searchResults.postValue(sortedItems)
                _hasMore.postValue(result.hasMore)
            } catch (e: Exception) {
                if (append && currentPage > 1) {
                    currentPage -= 1
                }
                if (!append) {
                    _searchResults.postValue(currentItems.toList())
                }
                _hasMore.postValue(false)
            } finally {
                requestInFlight = false
                _isLoading.postValue(false)
                _isLoadingMore.postValue(false)
            }
        }
    }

    sealed class SearchMode {
        data class Search(val query: String) : SearchMode()
        data class Category(val categoryName: String) : SearchMode()
    }

    private fun hasActiveTarget(): Boolean {
        return when (val mode = currentMode) {
            is SearchMode.Search -> mode.query.isNotBlank()
            is SearchMode.Category -> mode.categoryName.isNotBlank()
        }
    }

    companion object {
        private const val MAX_LOCAL_PREVIEW = 24
    }
}
