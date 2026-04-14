package com.example.wallpapiers.ui

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wallpapiers.R
import com.example.wallpapiers.adapter.CategoryAdapter
import com.example.wallpapiers.adapter.TrendingWallpaperAdapter
import com.example.wallpapiers.api.RetrofitInstance
import com.example.wallpapiers.databinding.FragmentHomeBinding
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.prefs.AppPreferences
import com.example.wallpapiers.repository.WallpaperRepository
import com.example.wallpapiers.util.WallpaperImageResolver
import com.example.wallpapiers.util.WallpaperRanking

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var trendingAdapter: TrendingWallpaperAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var appPreferences: AppPreferences
    private lateinit var repository: WallpaperRepository
    private var lastNavigationTimestamp = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreferences = AppPreferences(requireContext())
        repository = WallpaperRepository(
            RetrofitInstance.pexelsApi,
            RetrofitInstance.pixabayApi
        )
        val viewModelFactory = HomeViewModelFactory(repository, appPreferences)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        setupRecyclerViews()
        setupObservers()
        bindActions()
    }

    override fun onResume() {
        super.onResume()
        val showCredits = appPreferences.shouldShowPhotographerCredits()
        val dataSaver = appPreferences.isDataSaverEnabled()
        trendingAdapter.applyDisplayPreferences(showCredits, dataSaver)
        viewModel.refreshRankings()
    }

    private fun bindActions() {
        binding.btnSearch.setOnClickListener {
            navigateSafely(R.id.actionHomeFragmentToSearchFragment)
        }

        binding.btnSettings.setOnClickListener {
            navigateSafely(R.id.actionHomeFragmentToSettingsFragment)
        }

        binding.btnFeaturedAction.setOnClickListener {
            val wallpaper = viewModel.featuredWallpaper.value ?: return@setOnClickListener
            openWallpaper(wallpaper)
        }

        binding.btnViewCollections.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                initialQuery = "All",
                initialTitle = getString(R.string.view_collections),
                openAsCategory = true
            )
            navigateSafely(action)
        }
    }

    private fun setupRecyclerViews() {
        trendingAdapter = TrendingWallpaperAdapter { wallpaper ->
            openWallpaper(wallpaper)
        }
        binding.rvTrendingWallpapers.apply {
            adapter = trendingAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            openCategory(category.name)
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter = CategoryAdapter(categories) { category ->
                openCategory(category.name)
            }
            binding.rvCategories.adapter = categoryAdapter
        }

        viewModel.featuredWallpaper.observe(viewLifecycleOwner) { wallpaper ->
            bindFeaturedWallpaper(wallpaper)
        }

        viewModel.trendingWallpapers.observe(viewLifecycleOwner) { wallpapers ->
            trendingAdapter.submitList(wallpapers)
        }

        viewModel.totalWallpaperCount.observe(viewLifecycleOwner) { count ->
            binding.tvStatValueOne.text = "$count"
            binding.tvStatLabelOne.text = getString(R.string.stat_curated)
            binding.tvStatValueTwo.text = repository.configuredSourceCount().toString()
            binding.tvStatLabelTwo.text = getString(R.string.stat_sources)
            binding.tvStatValueThree.text = if (appPreferences.isDataSaverEnabled()) {
                getString(R.string.stat_mode_lite)
            } else {
                getString(R.string.stat_mode_hd)
            }
            binding.tvStatLabelThree.text = getString(R.string.stat_preview_mode)
            binding.tvTrendingCount.text = getString(R.string.wallpaper_count_label, count)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    private fun bindFeaturedWallpaper(wallpaper: Wallpaper?) {
        wallpaper ?: return

        binding.tvHeroEyebrow.text = wallpaper.collection ?: getString(R.string.app_name)
        binding.tvHeroTitle.text = WallpaperRanking.title(wallpaper)
        binding.tvHeroMeta.text = WallpaperRanking.subtitle(wallpaper)
        binding.tvFeaturedDownloadCount.text = WallpaperRanking.formatDownloads(wallpaper, appPreferences)
        binding.tvHeroSource.text = getString(R.string.hero_source_label, wallpaper.sourceName)

        com.bumptech.glide.Glide.with(binding.ivHeroWallpaper)
            .load(WallpaperImageResolver.heroUrl(wallpaper, appPreferences.isDataSaverEnabled()))
            .placeholder(android.R.color.darker_gray)
            .error(android.R.color.darker_gray)
            .into(binding.ivHeroWallpaper)
    }

    private fun openWallpaper(wallpaper: Wallpaper) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
            wallpaperId = wallpaper.id,
            wallpaperPreviewUrl = WallpaperImageResolver.previewUrl(wallpaper),
            wallpaperOriginalUrl = WallpaperImageResolver.downloadUrl(wallpaper),
            wallpaperPhotographer = wallpaper.photographer,
            wallpaperTitle = WallpaperRanking.title(wallpaper),
            wallpaperSourceName = wallpaper.sourceName
        )
        navigateSafely(action)
    }

    private fun openCategory(categoryName: String) {
        val query: String
        val title: String
        val asCategory: Boolean

        if (categoryName.equals("All", ignoreCase = true)) {
            query = "All"
            title = getString(R.string.view_collections)
            asCategory = true
        } else {
            query = categoryName
            title = "$categoryName collection"
            asCategory = true
        }

        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(
            initialQuery = query,
            initialTitle = title,
            openAsCategory = asCategory
        )
        navigateSafely(action)
    }

    private fun navigateSafely(actionId: Int) {
        if (!isAdded || _binding == null) return
        if (!canNavigate()) return
        try {
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.homeFragment) {
                navController.navigate(actionId)
            }
        } catch (_: Exception) { }
    }

    private fun navigateSafely(directions: NavDirections) {
        if (!isAdded || _binding == null) return
        if (!canNavigate()) return
        try {
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.homeFragment) {
                navController.navigate(directions)
            }
        } catch (_: Exception) { }
    }

    private fun canNavigate(): Boolean {
        val now = SystemClock.elapsedRealtime()
        if (now - lastNavigationTimestamp < NAVIGATION_DEBOUNCE_MS) return false
        lastNavigationTimestamp = now
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val NAVIGATION_DEBOUNCE_MS = 700L
    }
}
