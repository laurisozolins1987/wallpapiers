package com.example.wallpapiers.ui

import android.app.DownloadManager
import android.app.WallpaperManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.wallpapiers.R
import com.example.wallpapiers.databinding.FragmentDetailBinding
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.model.WallpaperSrc
import com.example.wallpapiers.prefs.AppPreferences
import com.example.wallpapiers.util.WallpaperImageResolver
import com.example.wallpapiers.util.WallpaperRanking
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var appPreferences: AppPreferences
    private var isActionInFlight = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreferences = AppPreferences(requireContext())
        val imageUrl = WallpaperImageResolver.detailUrl(
            args.wallpaperPreviewUrl,
            args.wallpaperOriginalUrl
        )

        Glide.with(this)
            .load(imageUrl)
            .placeholder(android.R.color.black)
            .error(android.R.color.darker_gray)
            .into(binding.ivFullWallpaper)

        binding.tvWallpaperTitle.text = args.wallpaperTitle
        binding.tvWallpaperMeta.text = getString(
            R.string.detail_meta_label,
            args.wallpaperPhotographer,
            args.wallpaperSourceName
        )
        updateRankText()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnDownloadIcon.setOnClickListener {
            downloadWallpaper(
                WallpaperImageResolver.actionUrl(
                    args.wallpaperOriginalUrl,
                    args.wallpaperPreviewUrl
                )
            )
        }

        binding.btnDownload.setOnClickListener {
            downloadWallpaper(
                WallpaperImageResolver.actionUrl(
                    args.wallpaperOriginalUrl,
                    args.wallpaperPreviewUrl
                )
            )
        }

        binding.btnSetWallpaper.setOnClickListener {
            maybeSetWallpaper(
                WallpaperImageResolver.actionUrl(
                    args.wallpaperOriginalUrl,
                    args.wallpaperPreviewUrl
                )
            )
        }
    }

    private fun maybeSetWallpaper(url: String) {
        if (url.isBlank()) {
            showCustomToast(getString(R.string.wallpaper_image_unavailable))
            return
        }

        if (!appPreferences.shouldConfirmBeforeApplying()) {
            setWallpaper(url)
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.apply_wallpaper_title)
            .setMessage(R.string.apply_wallpaper_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.apply) { _, _ ->
                setWallpaper(url)
            }
            .show()
    }

    private fun setWallpaper(url: String) {
        if (isActionInFlight) return

        showCustomToast(getString(R.string.wallpaper_downloading))
        setActionState(isBusy = true)

        val appContext = requireContext().applicationContext
        val (targetWidth, targetHeight) = resolveWallpaperTargetSize()

        viewLifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val futureTarget = Glide.with(appContext)
                        .asBitmap()
                        .load(url)
                        .submit(targetWidth, targetHeight)

                    try {
                        val bitmap = futureTarget.get()
                        WallpaperManager.getInstance(appContext).setBitmap(bitmap)
                    } finally {
                        withContext(Dispatchers.Main) {
                            Glide.with(appContext).clear(futureTarget)
                        }
                    }
                }
            }

            if (!isAdded || _binding == null) return@launch

            setActionState(isBusy = false)
            if (result.isSuccess) {
                appPreferences.recordApply(args.wallpaperId)
                updateRankText()
                showCustomToast(getString(R.string.wallpaper_set_success))
            } else {
                showCustomToast(getString(R.string.wallpaper_set_error))
            }
        }
    }

    private fun downloadWallpaper(url: String) {
        if (url.isBlank()) {
            showCustomToast(getString(R.string.wallpaper_image_unavailable))
            return
        }

        val downloadManager = requireContext().getSystemService(DownloadManager::class.java)
        val safeTitle = args.wallpaperTitle.lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .ifBlank { "wallpaper-${args.wallpaperId.takeLast(8)}" }
        val fileName = "$safeTitle-${args.wallpaperId}.jpg"

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(args.wallpaperTitle)
            .setDescription(getString(R.string.download_description))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalFilesDir(
                requireContext(),
                Environment.DIRECTORY_PICTURES,
                fileName
            )

        runCatching {
            downloadManager.enqueue(request)
        }.onSuccess {
            appPreferences.recordDownload(args.wallpaperId)
            updateRankText()
            showCustomToast(getString(R.string.download_queued))
        }.onFailure {
            showCustomToast(getString(R.string.download_failed))
        }
    }

    private fun updateRankText() {
        binding.tvWallpaperRank.text = WallpaperRanking.formatDownloads(currentWallpaper(), appPreferences)
    }

    private fun setActionState(isBusy: Boolean) {
        isActionInFlight = isBusy
        binding.detailProgress.isVisible = isBusy
        binding.btnSetWallpaper.isEnabled = !isBusy
        binding.btnDownload.isEnabled = !isBusy
        binding.btnDownloadIcon.isEnabled = !isBusy
    }

    private fun resolveWallpaperTargetSize(): Pair<Int, Int> {
        val displayMetrics = resources.displayMetrics
        val shortSide = min(displayMetrics.widthPixels, displayMetrics.heightPixels).coerceAtLeast(1080)
        val longSide = max(displayMetrics.widthPixels, displayMetrics.heightPixels).coerceAtLeast(1920)
        return shortSide to longSide
    }

    private fun currentWallpaper(): Wallpaper {
        return Wallpaper(
            id = args.wallpaperId,
            width = 1440,
            height = 2560,
            url = args.wallpaperPreviewUrl,
            photographer = args.wallpaperPhotographer,
            provider = com.example.wallpapiers.model.WallpaperProvider.PEXELS,
            remoteId = args.wallpaperId,
            src = WallpaperSrc(
                original = args.wallpaperOriginalUrl,
                large2x = args.wallpaperOriginalUrl,
                large = args.wallpaperPreviewUrl,
                medium = args.wallpaperPreviewUrl,
                small = args.wallpaperPreviewUrl,
                portrait = args.wallpaperPreviewUrl,
                landscape = args.wallpaperPreviewUrl,
                tiny = args.wallpaperPreviewUrl
            ),
            title = args.wallpaperTitle,
            sourceName = args.wallpaperSourceName
        )
    }

    private fun showCustomToast(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
