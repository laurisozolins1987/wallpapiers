package com.example.wallpapiers.repository

import com.example.wallpapiers.BuildConfig
import com.example.wallpapiers.api.PexelsApi
import com.example.wallpapiers.api.PixabayApi
import com.example.wallpapiers.data.LocalWallpaperCatalog
import com.example.wallpapiers.model.Category
import com.example.wallpapiers.model.PexelsPhoto
import com.example.wallpapiers.model.PixabayImage
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.model.WallpaperPage
import com.example.wallpapiers.model.WallpaperProvider
import com.example.wallpapiers.model.WallpaperSortOption
import com.example.wallpapiers.model.WallpaperSourceFilter
import com.example.wallpapiers.model.WallpaperSrc
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException

class WallpaperRepository(
    private val pexelsApi: PexelsApi,
    private val pixabayApi: PixabayApi
) {
    private val pexelsApiKey = BuildConfig.PEXELS_API_KEY
    private val pixabayApiKey = BuildConfig.PIXABAY_API_KEY

    fun getCategories(): List<Category> = LocalWallpaperCatalog.categories

    fun getCategory(name: String): Category? = LocalWallpaperCatalog.getCategory(name)

    fun configuredSourceCount(): Int {
        var count = 1 // Local curated fallback is always available
        if (hasPexels()) count += 1
        if (hasPixabay()) count += 1
        return count
    }

    suspend fun getHomeFeed(): List<Wallpaper> = supervisorScope {
        val tasks = mutableListOf<kotlinx.coroutines.Deferred<WallpaperPage>>()

        if (hasPexels()) {
            tasks += async { fetchPexelsCurated(page = 1) }
            HOME_PEXELS_QUERIES.forEach { query ->
                tasks += async { fetchPexelsSearch(query = query, page = 1) }
            }
        }

        if (hasPixabay()) {
            HOME_PIXABAY_CATEGORIES.forEach { category ->
                tasks += async {
                    fetchPixabayCategory(
                        category = category,
                        page = 1,
                        sortOption = WallpaperSortOption.POPULAR
                    )
                }
            }
        }

        val remoteItems = awaitWallpaperPages(tasks).flatMap { it.items }

        (LocalWallpaperCatalog.wallpapers + remoteItems)
            .distinctBy { it.id }
            .take(HOME_FEED_MAX_ITEMS)
    }

    suspend fun searchWallpapers(
        query: String,
        page: Int,
        sortOption: WallpaperSortOption,
        sourceFilter: WallpaperSourceFilter
    ): WallpaperPage = supervisorScope {
        val normalizedQuery = query.trim()
        val tasks = mutableListOf<kotlinx.coroutines.Deferred<WallpaperPage>>()

        if (shouldUse(sourceFilter, WallpaperProvider.PEXELS) && hasPexels()) {
            tasks += async { fetchPexelsSearch(query = normalizedQuery, page = page) }
        }
        if (shouldUse(sourceFilter, WallpaperProvider.PIXABAY) && hasPixabay()) {
            tasks += async { fetchPixabaySearch(query = normalizedQuery, page = page, sortOption = sortOption) }
        }

        val remoteResults = awaitWallpaperPages(tasks)
        val localResults = if (page == 1 && shouldIncludeLocal(sourceFilter)) {
            LocalWallpaperCatalog.search(normalizedQuery)
        } else {
            emptyList()
        }

        WallpaperPage(
            items = (localResults + remoteResults.flatMap { it.items }).distinctBy { it.id },
            hasMore = remoteResults.any { it.hasMore }
        )
    }

    suspend fun getWallpapersByCategory(
        categoryName: String,
        page: Int,
        sortOption: WallpaperSortOption,
        sourceFilter: WallpaperSourceFilter
    ): WallpaperPage = supervisorScope {
        val category = getCategory(categoryName) ?: return@supervisorScope WallpaperPage(emptyList(), hasMore = false)
        val tasks = mutableListOf<kotlinx.coroutines.Deferred<WallpaperPage>>()

        if (shouldUse(sourceFilter, WallpaperProvider.PEXELS) && hasPexels()) {
            tasks += async { fetchPexelsSearch(query = category.query, page = page) }
        }
        if (shouldUse(sourceFilter, WallpaperProvider.PIXABAY) && hasPixabay()) {
            tasks += async {
                if (category.pixabayCategory != null) {
                    fetchPixabayCategory(category.pixabayCategory, page, sortOption)
                } else {
                    fetchPixabaySearch(category.query, page, sortOption)
                }
            }
        }

        val remoteResults = awaitWallpaperPages(tasks)
        val localResults = if (page == 1 && shouldIncludeLocal(sourceFilter)) {
            LocalWallpaperCatalog.wallpapersForCategory(category.name)
        } else {
            emptyList()
        }

        WallpaperPage(
            items = (localResults + remoteResults.flatMap { it.items }).distinctBy { it.id },
            hasMore = remoteResults.any { it.hasMore }
        )
    }

    private suspend fun fetchPexelsCurated(page: Int): WallpaperPage {
        val response = runCatching {
            pexelsApi.getCuratedWallpapers(pexelsApiKey, page)
        }.getOrNull()

        val body = response?.body()
        return WallpaperPage(
            items = body?.photos.orEmpty().map { it.toWallpaper() },
            hasMore = !body?.nextPage.isNullOrBlank()
        )
    }

    private suspend fun fetchPexelsSearch(query: String, page: Int): WallpaperPage {
        val response = runCatching {
            pexelsApi.searchWallpapers(pexelsApiKey, query, page)
        }.getOrNull()

        val body = response?.body()
        return WallpaperPage(
            items = body?.photos.orEmpty().map { it.toWallpaper() },
            hasMore = !body?.nextPage.isNullOrBlank()
        )
    }

    private suspend fun fetchPixabaySearch(
        query: String,
        page: Int,
        sortOption: WallpaperSortOption
    ): WallpaperPage {
        val response = runCatching {
            pixabayApi.searchImages(
                apiKey = pixabayApiKey,
                query = query,
                page = page,
                perPage = PIXABAY_PAGE_SIZE,
                order = pixabayOrder(sortOption)
            )
        }.getOrNull()

        val body = response?.body()
        return WallpaperPage(
            items = body?.hits.orEmpty().map { it.toWallpaper() },
            hasMore = body != null && (page * PIXABAY_PAGE_SIZE) < body.totalHits
        )
    }

    private suspend fun fetchPixabayCategory(
        category: String,
        page: Int,
        sortOption: WallpaperSortOption
    ): WallpaperPage {
        val response = runCatching {
            pixabayApi.searchImages(
                apiKey = pixabayApiKey,
                category = category,
                page = page,
                perPage = PIXABAY_PAGE_SIZE,
                order = pixabayOrder(sortOption)
            )
        }.getOrNull()

        val body = response?.body()
        return WallpaperPage(
            items = body?.hits.orEmpty().map { it.toWallpaper(collection = category.replaceFirstChar { ch -> ch.titlecase() }) },
            hasMore = body != null && (page * PIXABAY_PAGE_SIZE) < body.totalHits
        )
    }

    private fun shouldUse(sourceFilter: WallpaperSourceFilter, provider: WallpaperProvider): Boolean {
        return sourceFilter == WallpaperSourceFilter.ALL || sourceFilter.provider == provider
    }

    private fun shouldIncludeLocal(sourceFilter: WallpaperSourceFilter): Boolean {
        return sourceFilter != WallpaperSourceFilter.PIXABAY
    }

    private suspend fun awaitWallpaperPages(
        tasks: List<kotlinx.coroutines.Deferred<WallpaperPage>>
    ): List<WallpaperPage> {
        if (tasks.isEmpty()) return emptyList()

        return tasks.map { deferred ->
            withTimeoutOrNull(API_TIMEOUT_MS) {
                try {
                    deferred.await()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    WallpaperPage(emptyList(), false)
                }
            } ?: WallpaperPage(emptyList(), false)
        }
    }

    private fun hasPexels(): Boolean = pexelsApiKey.isNotBlank()

    private fun hasPixabay(): Boolean = pixabayApiKey.isNotBlank()

    private fun pixabayOrder(sortOption: WallpaperSortOption): String {
        return if (sortOption == WallpaperSortOption.POPULAR) "popular" else "latest"
    }

    private fun PexelsPhoto.toWallpaper(collection: String? = guessCollection(alt)): Wallpaper {
        return Wallpaper(
            id = "pexels_$id",
            remoteId = id.toString(),
            provider = WallpaperProvider.PEXELS,
            width = width,
            height = height,
            url = url,
            photographer = photographer,
            photographerUrl = photographerUrl,
            src = src,
            title = alt,
            collection = collection,
            tags = listOfNotNull(collection, alt?.takeIf { it.isNotBlank() }),
            sourceName = WallpaperProvider.PEXELS.label,
            sourceUrl = url
        )
    }

    private fun PixabayImage.toWallpaper(collection: String? = guessCollection(tags)): Wallpaper {
        val originalUrl = fullHdUrl ?: largeImageUrl ?: imageUrl ?: webformatUrl ?: previewUrl.orEmpty()
        val mediumUrl = webformatUrl ?: previewUrl ?: originalUrl
        val portraitUrl = webformatUrl ?: largeImageUrl ?: originalUrl

        return Wallpaper(
            id = "pixabay_$id",
            remoteId = id.toString(),
            provider = WallpaperProvider.PIXABAY,
            width = imageWidth,
            height = imageHeight,
            url = pageUrl,
            photographer = user.ifBlank { "Pixabay creator" },
            photographerUrl = if (userId != 0) "https://pixabay.com/users/$user-$userId/" else null,
            src = WallpaperSrc(
                original = originalUrl,
                large2x = originalUrl,
                large = largeImageUrl ?: mediumUrl,
                medium = mediumUrl,
                small = previewUrl ?: mediumUrl,
                portrait = portraitUrl,
                landscape = largeImageUrl ?: mediumUrl,
                tiny = previewUrl ?: mediumUrl
            ),
            title = tags.split(",").firstOrNull()?.trim(),
            collection = collection,
            downloads = downloads,
            views = views,
            likes = likes,
            tags = tags.split(",").mapNotNull { it.trim().takeIf(String::isNotBlank) },
            sourceName = WallpaperProvider.PIXABAY.label,
            sourceUrl = pageUrl
        )
    }

    private fun guessCollection(value: String?): String? {
        val normalized = value.orEmpty().lowercase()
        return getCategories()
            .firstOrNull { category ->
                category.name != DEFAULT_CATEGORY && (
                    normalized.contains(category.name.lowercase()) ||
                        normalized.contains(category.query.substringBefore(" wallpaper").trim().lowercase())
                    )
            }
            ?.name
    }

    companion object {
        private const val PIXABAY_PAGE_SIZE = 20
        private const val HOME_FEED_MAX_ITEMS = 60
        private const val DEFAULT_CATEGORY = "All"
        private const val API_TIMEOUT_MS = 5000L
        private val HOME_PEXELS_QUERIES = listOf(
            "nature wallpaper"
        )
        private val HOME_PIXABAY_CATEGORIES = listOf(
            "backgrounds",
            "nature"
        )
    }
}
