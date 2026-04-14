package com.example.wallpapiers.ui

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpapiers.R
import com.example.wallpapiers.adapter.WallpaperAdapter
import com.example.wallpapiers.api.RetrofitInstance
import com.example.wallpapiers.databinding.FragmentSearchBinding
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.model.WallpaperSortOption
import com.example.wallpapiers.model.WallpaperSourceFilter
import com.example.wallpapiers.prefs.AppPreferences
import com.example.wallpapiers.repository.WallpaperRepository
import com.example.wallpapiers.util.WallpaperImageResolver
import com.example.wallpapiers.util.WallpaperRanking

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SearchViewModel
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private lateinit var appPreferences: AppPreferences
    private lateinit var gridLayoutManager: GridLayoutManager
    private val args: SearchFragmentArgs by navArgs()
    private var lastNavigationTimestamp = 0L
    private var isCategoryMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreferences = AppPreferences(requireContext())
        val repository = WallpaperRepository(
            RetrofitInstance.pexelsApi,
            RetrofitInstance.pixabayApi
        )
        val viewModelFactory = SearchViewModelFactory(repository, appPreferences)
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]

        isCategoryMode = args.openAsCategory && args.initialQuery.isNotBlank()

        setupRecyclerView()
        setupObservers()
        configureMode()

        // Set chip defaults BEFORE attaching listeners to avoid race conditions
        binding.chipSortTrending.isChecked = true
        binding.chipSourceAll.isChecked = true

        setupActions()
        applyDisplayPreferences()

        // Start loading content
        if (args.initialQuery.isNotBlank()) {
            if (!isCategoryMode) {
                binding.etSearch.setText(args.initialQuery)
            }
            viewModel.start(args.initialQuery, args.initialTitle, args.openAsCategory)
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            applyDisplayPreferences()
        }
    }

    private fun configureMode() {
        if (isCategoryMode) {
            binding.searchSection.isVisible = false
            binding.categoryHeader.isVisible = true
            binding.tvCategoryTitle.text = args.initialTitle.ifBlank { args.initialQuery }
            binding.tvCategorySubtitle.text = getString(R.string.category_browse_subtitle)
            binding.emptyStateGroup.isVisible = false
        } else {
            binding.searchSection.isVisible = true
            binding.categoryHeader.isVisible = false
        }
    }

    private fun setupActions() {
        binding.toolbar.setNavigationOnClickListener {
            if (isAdded) findNavController().navigateUp()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                runSearch(binding.etSearch.text?.toString().orEmpty())
                true
            } else {
                false
            }
        }

        binding.btnSearch.setOnClickListener {
            runSearch(binding.etSearch.text?.toString().orEmpty())
        }

        binding.btnLoadMore.setOnClickListener {
            viewModel.loadMore()
        }

        val quickSearchListener = View.OnClickListener { selectedView ->
            val query = when (selectedView.id) {
                R.id.chipNature -> "Nature"
                R.id.chipAmoled -> "AMOLED"
                R.id.chipMinimal -> "Minimal"
                R.id.chipSpace -> "Space"
                R.id.chipCars -> "Cars"
                else -> "Technology"
            }
            binding.etSearch.setText(query)
            runSearch(query)
        }

        listOf(
            binding.chipNature,
            binding.chipAmoled,
            binding.chipMinimal,
            binding.chipSpace,
            binding.chipCars,
            binding.chipTechnology
        ).forEach { chip ->
            chip.setOnClickListener(quickSearchListener)
        }

        // Attach listeners AFTER chip defaults are set
        binding.sortGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val sortOption = when (checkedId) {
                R.id.chipSortPopular -> WallpaperSortOption.POPULAR
                R.id.chipSortCreator -> WallpaperSortOption.CREATOR
                R.id.chipSortTall -> WallpaperSortOption.TALL
                R.id.chipSortSource -> WallpaperSortOption.SOURCE
                else -> WallpaperSortOption.TRENDING
            }
            viewModel.applySort(sortOption)
        }

        binding.sourceGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val sourceFilter = when (checkedId) {
                R.id.chipSourcePexels -> WallpaperSourceFilter.PEXELS
                R.id.chipSourcePixabay -> WallpaperSourceFilter.PIXABAY
                else -> WallpaperSourceFilter.ALL
            }
            viewModel.applySourceFilter(sourceFilter)
        }
    }

    private fun setupRecyclerView() {
        wallpaperAdapter = WallpaperAdapter { wallpaper ->
            openWallpaper(wallpaper)
        }
        gridLayoutManager = GridLayoutManager(requireContext(), appPreferences.getGridSpanCount())
        binding.rvSearchResults.apply {
            adapter = wallpaperAdapter
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
            setItemViewCacheSize(8)
        }
    }

    private fun setupObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner) { wallpapers ->
            if (_binding == null) return@observe
            wallpaperAdapter.submitList(wallpapers)
            binding.tvResultsSummary.text = if (wallpapers.isEmpty()) {
                getString(R.string.search_empty_title)
            } else {
                getString(R.string.search_results_count_label, wallpapers.size)
            }
            if (!isCategoryMode) {
                binding.emptyStateGroup.isVisible = wallpapers.isEmpty()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (_binding == null) return@observe
            binding.progressBar.isVisible = isLoading
        }

        viewModel.isLoadingMore.observe(viewLifecycleOwner) { isLoadingMore ->
            if (_binding == null) return@observe
            binding.btnLoadMore.isEnabled = !isLoadingMore
            binding.btnLoadMore.text = if (isLoadingMore) {
                getString(R.string.loading_more)
            } else {
                getString(R.string.load_more)
            }
        }

        viewModel.hasMore.observe(viewLifecycleOwner) { hasMore ->
            if (_binding == null) return@observe
            binding.btnLoadMore.isVisible = hasMore
        }

        viewModel.screenTitle.observe(viewLifecycleOwner) { title ->
            if (_binding == null) return@observe
            binding.toolbar.title = title
        }
    }

    private fun runSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return
        binding.emptyStateGroup.isVisible = false
        viewModel.searchWallpapers(trimmedQuery)
    }

    private fun applyDisplayPreferences() {
        gridLayoutManager.spanCount = appPreferences.getGridSpanCount()
        wallpaperAdapter.applyDisplayPreferences(
            showCredits = appPreferences.shouldShowPhotographerCredits(),
            dataSaverEnabled = appPreferences.isDataSaverEnabled()
        )
    }

    private fun openWallpaper(wallpaper: Wallpaper) {
        if (!isAdded || _binding == null) return
        val now = SystemClock.elapsedRealtime()
        if (now - lastNavigationTimestamp < 700L) return
        lastNavigationTimestamp = now

        try {
            val navController = findNavController()
            if (navController.currentDestination?.id != R.id.searchFragment) return
            val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(
                wallpaperId = wallpaper.id,
                wallpaperPreviewUrl = WallpaperImageResolver.previewUrl(wallpaper),
                wallpaperOriginalUrl = WallpaperImageResolver.downloadUrl(wallpaper),
                wallpaperPhotographer = wallpaper.photographer,
                wallpaperTitle = WallpaperRanking.title(wallpaper),
                wallpaperSourceName = wallpaper.sourceName
            )
            navController.navigate(action)
        } catch (_: Exception) { }
    }

    override fun onDestroyView() {
        binding.rvSearchResults.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
