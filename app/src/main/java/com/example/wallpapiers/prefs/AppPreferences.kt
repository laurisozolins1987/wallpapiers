package com.example.wallpapiers.prefs

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class AppPreferences(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    enum class ThemeMode(val storageValue: String, val nightMode: Int) {
        SYSTEM("system", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
        LIGHT("light", AppCompatDelegate.MODE_NIGHT_NO),
        DARK("dark", AppCompatDelegate.MODE_NIGHT_YES);

        companion object {
            fun from(storageValue: String?): ThemeMode {
                return entries.firstOrNull { it.storageValue == storageValue } ?: SYSTEM
            }
        }
    }

    fun getThemeMode(): ThemeMode = ThemeMode.from(prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.storageValue))

    fun setThemeMode(themeMode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, themeMode.storageValue).apply()
    }

    fun isDenseGridEnabled(): Boolean = prefs.getBoolean(KEY_DENSE_GRID, false)

    fun setDenseGridEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DENSE_GRID, enabled).apply()
    }

    fun isDataSaverEnabled(): Boolean = prefs.getBoolean(KEY_DATA_SAVER, false)

    fun setDataSaverEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DATA_SAVER, enabled).apply()
    }

    fun shouldShowPhotographerCredits(): Boolean = prefs.getBoolean(KEY_SHOW_CREDITS, true)

    fun setShowPhotographerCredits(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_CREDITS, enabled).apply()
    }

    fun shouldConfirmBeforeApplying(): Boolean = prefs.getBoolean(KEY_CONFIRM_APPLY, true)

    fun setConfirmBeforeApplying(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CONFIRM_APPLY, enabled).apply()
    }

    fun getGridSpanCount(): Int = if (isDenseGridEnabled()) 3 else 2

    fun getWallpaperDownloads(wallpaperId: String): Int = prefs.getInt("${KEY_DOWNLOAD_PREFIX}_$wallpaperId", 0)

    fun getWallpaperApplies(wallpaperId: String): Int = prefs.getInt("${KEY_APPLY_PREFIX}_$wallpaperId", 0)

    fun recordDownload(wallpaperId: String) {
        prefs.edit()
            .putInt("${KEY_DOWNLOAD_PREFIX}_$wallpaperId", getWallpaperDownloads(wallpaperId) + 1)
            .putInt(KEY_TOTAL_DOWNLOADS, getTotalDownloads() + 1)
            .apply()
    }

    fun recordApply(wallpaperId: String) {
        prefs.edit()
            .putInt("${KEY_APPLY_PREFIX}_$wallpaperId", getWallpaperApplies(wallpaperId) + 1)
            .putInt(KEY_TOTAL_APPLIES, getTotalApplies() + 1)
            .apply()
    }

    fun getTotalDownloads(): Int = prefs.getInt(KEY_TOTAL_DOWNLOADS, 0)

    fun getTotalApplies(): Int = prefs.getInt(KEY_TOTAL_APPLIES, 0)

    fun getInteractionBoost(wallpaperId: String): Int {
        return getWallpaperDownloads(wallpaperId) * 2200 + getWallpaperApplies(wallpaperId) * 2800
    }

    fun clearPersonalization() {
        val preservedTheme = getThemeMode()
        val preservedDenseGrid = isDenseGridEnabled()
        val preservedDataSaver = isDataSaverEnabled()
        val preservedCredits = shouldShowPhotographerCredits()
        val preservedConfirmApply = shouldConfirmBeforeApplying()

        prefs.edit()
            .clear()
            .putString(KEY_THEME_MODE, preservedTheme.storageValue)
            .putBoolean(KEY_DENSE_GRID, preservedDenseGrid)
            .putBoolean(KEY_DATA_SAVER, preservedDataSaver)
            .putBoolean(KEY_SHOW_CREDITS, preservedCredits)
            .putBoolean(KEY_CONFIRM_APPLY, preservedConfirmApply)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "wallpapers_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DENSE_GRID = "dense_grid"
        private const val KEY_DATA_SAVER = "data_saver"
        private const val KEY_SHOW_CREDITS = "show_credits"
        private const val KEY_CONFIRM_APPLY = "confirm_apply"
        private const val KEY_TOTAL_DOWNLOADS = "total_downloads"
        private const val KEY_TOTAL_APPLIES = "total_applies"
        private const val KEY_DOWNLOAD_PREFIX = "wallpaper_download"
        private const val KEY_APPLY_PREFIX = "wallpaper_apply"
    }
}
