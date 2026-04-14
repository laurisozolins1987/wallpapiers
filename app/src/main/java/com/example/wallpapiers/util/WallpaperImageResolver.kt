package com.example.wallpapiers.util

import com.example.wallpapiers.model.Wallpaper

object WallpaperImageResolver {

    fun feedUrl(wallpaper: Wallpaper, dataSaverEnabled: Boolean): String {
        return if (dataSaverEnabled) {
            firstNonBlank(
                wallpaper.src.tiny,
                wallpaper.src.small,
                wallpaper.src.medium,
                wallpaper.src.portrait,
                wallpaper.src.large,
                wallpaper.src.original,
                wallpaper.url
            )
        } else {
            firstNonBlank(
                wallpaper.src.small,
                wallpaper.src.medium,
                wallpaper.src.portrait,
                wallpaper.src.large,
                wallpaper.src.large2x,
                wallpaper.src.original,
                wallpaper.url
            )
        }
    }

    fun trendingUrl(wallpaper: Wallpaper, dataSaverEnabled: Boolean): String {
        return if (dataSaverEnabled) {
            firstNonBlank(
                wallpaper.src.small,
                wallpaper.src.medium,
                wallpaper.src.portrait,
                wallpaper.src.large,
                wallpaper.src.original,
                wallpaper.url
            )
        } else {
            firstNonBlank(
                wallpaper.src.large,
                wallpaper.src.portrait,
                wallpaper.src.large2x,
                wallpaper.src.medium,
                wallpaper.src.original,
                wallpaper.url
            )
        }
    }

    fun heroUrl(wallpaper: Wallpaper, dataSaverEnabled: Boolean): String {
        return if (dataSaverEnabled) {
            firstNonBlank(
                wallpaper.src.medium,
                wallpaper.src.portrait,
                wallpaper.src.large,
                wallpaper.src.original,
                wallpaper.url
            )
        } else {
            firstNonBlank(
                wallpaper.src.large2x,
                wallpaper.src.portrait,
                wallpaper.src.large,
                wallpaper.src.original,
                wallpaper.src.medium,
                wallpaper.url
            )
        }
    }

    fun previewUrl(wallpaper: Wallpaper): String {
        return firstNonBlank(
            wallpaper.src.portrait,
            wallpaper.src.large,
            wallpaper.src.medium,
            wallpaper.src.small,
            wallpaper.src.original,
            wallpaper.url
        )
    }

    fun downloadUrl(wallpaper: Wallpaper): String {
        return firstNonBlank(
            wallpaper.src.original,
            wallpaper.src.large2x,
            wallpaper.src.large,
            wallpaper.src.portrait,
            wallpaper.src.medium,
            wallpaper.src.small,
            wallpaper.url
        )
    }

    fun detailUrl(previewUrl: String?, originalUrl: String?): String {
        return firstNonBlank(previewUrl, originalUrl)
    }

    fun actionUrl(originalUrl: String?, previewUrl: String?): String {
        return firstNonBlank(originalUrl, previewUrl)
    }

    private fun firstNonBlank(vararg values: String?): String {
        return values.firstOrNull { !it.isNullOrBlank() }.orEmpty()
    }
}
