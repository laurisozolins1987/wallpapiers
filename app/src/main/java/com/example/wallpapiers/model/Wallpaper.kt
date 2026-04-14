package com.example.wallpapiers.model

import com.google.gson.annotations.SerializedName

data class PexelsResponse(
    val photos: List<PexelsPhoto>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("next_page") val nextPage: String? = null
)

data class PexelsPhoto(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerializedName("photographer_url") val photographerUrl: String? = null,
    @SerializedName("avg_color") val avgColor: String? = null,
    val src: WallpaperSrc,
    val alt: String? = null
)

data class PixabayResponse(
    val total: Int = 0,
    @SerializedName("totalHits") val totalHits: Int = 0,
    val hits: List<PixabayImage> = emptyList()
)

data class PixabayImage(
    val id: Int,
    @SerializedName("pageURL") val pageUrl: String,
    val tags: String = "",
    @SerializedName("previewURL") val previewUrl: String? = null,
    @SerializedName("webformatURL") val webformatUrl: String? = null,
    @SerializedName("largeImageURL") val largeImageUrl: String? = null,
    @SerializedName("fullHDURL") val fullHdUrl: String? = null,
    @SerializedName("imageURL") val imageUrl: String? = null,
    @SerializedName("imageWidth") val imageWidth: Int = 0,
    @SerializedName("imageHeight") val imageHeight: Int = 0,
    val downloads: Int = 0,
    val views: Int = 0,
    val likes: Int = 0,
    val comments: Int = 0,
    val user: String = "",
    @SerializedName("user_id") val userId: Int = 0
)



enum class WallpaperProvider(val label: String) {
    PEXELS("Pexels"),
    PIXABAY("Pixabay")
}

enum class WallpaperSourceFilter(val label: String, val provider: WallpaperProvider?) {
    ALL("All", null),
    PEXELS("Pexels", WallpaperProvider.PEXELS),
    PIXABAY("Pixabay", WallpaperProvider.PIXABAY)
}

enum class WallpaperSortOption(val label: String) {
    TRENDING("Trending"),
    POPULAR("Popular"),
    CREATOR("Creator"),
    TALL("Tall"),
    SOURCE("Source");

    companion object {
        fun fromLabel(label: String?): WallpaperSortOption {
            return entries.firstOrNull { it.label.equals(label, ignoreCase = true) } ?: TRENDING
        }
    }
}

data class Wallpaper(
    val id: String,
    val remoteId: String,
    val provider: WallpaperProvider,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String? = null,
    val src: WallpaperSrc,
    val title: String? = null,
    val collection: String? = null,
    val downloads: Int? = null,
    val views: Int? = null,
    val likes: Int? = null,
    val tags: List<String> = emptyList(),
    val sourceName: String = provider.label,
    val sourceUrl: String? = null,
    val attribution: String? = null
)

data class WallpaperSrc(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)

data class Category(
    val name: String,
    val imageUrl: String,
    val query: String = name,
    val pixabayCategory: String? = null
)

data class WallpaperPage(
    val items: List<Wallpaper>,
    val hasMore: Boolean
)
