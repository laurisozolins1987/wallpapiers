package com.example.wallpapiers.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wallpapiers.R
import com.example.wallpapiers.databinding.FragmentSettingsBinding
import com.example.wallpapiers.prefs.AppPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appPreferences = AppPreferences(requireContext())

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bindCurrentPreferences()
        bindActions()
        updatePersonalizationSummary()
    }

    private fun bindCurrentPreferences() {
        when (appPreferences.getThemeMode()) {
            AppPreferences.ThemeMode.SYSTEM -> binding.toggleTheme.check(R.id.btnThemeSystem)
            AppPreferences.ThemeMode.LIGHT -> binding.toggleTheme.check(R.id.btnThemeLight)
            AppPreferences.ThemeMode.DARK -> binding.toggleTheme.check(R.id.btnThemeDark)
        }

        if (appPreferences.isDenseGridEnabled()) {
            binding.toggleGrid.check(R.id.btnGridDense)
        } else {
            binding.toggleGrid.check(R.id.btnGridComfort)
        }

        binding.switchDataSaver.isChecked = appPreferences.isDataSaverEnabled()
        binding.switchCredits.isChecked = appPreferences.shouldShowPhotographerCredits()
        binding.switchConfirmApply.isChecked = appPreferences.shouldConfirmBeforeApplying()
    }

    private fun bindActions() {
        binding.toggleTheme.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val themeMode = when (checkedId) {
                R.id.btnThemeLight -> AppPreferences.ThemeMode.LIGHT
                R.id.btnThemeDark -> AppPreferences.ThemeMode.DARK
                else -> AppPreferences.ThemeMode.SYSTEM
            }
            appPreferences.setThemeMode(themeMode)
            AppCompatDelegate.setDefaultNightMode(themeMode.nightMode)
        }

        binding.toggleGrid.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            appPreferences.setDenseGridEnabled(checkedId == R.id.btnGridDense)
        }

        binding.switchDataSaver.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setDataSaverEnabled(isChecked)
        }

        binding.switchCredits.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setShowPhotographerCredits(isChecked)
        }

        binding.switchConfirmApply.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.setConfirmBeforeApplying(isChecked)
        }

        binding.btnResetPersonalization.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.reset_personalization_title)
                .setMessage(R.string.reset_personalization_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.reset) { _, _ ->
                    appPreferences.clearPersonalization()
                    bindCurrentPreferences()
                    updatePersonalizationSummary()
                }
                .show()
        }
    }

    private fun updatePersonalizationSummary() {
        binding.tvPersonalizationSummary.text = getString(
            R.string.personalization_summary,
            appPreferences.getTotalDownloads(),
            appPreferences.getTotalApplies()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
