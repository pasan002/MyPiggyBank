package com.example.mypiggybank.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mypiggybank.R
import com.example.mypiggybank.data.PreferencesManager
import com.example.mypiggybank.data.repository.TransactionRepository
import com.example.mypiggybank.utils.BackupManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var transactionRepository: TransactionRepository

    @Inject
    lateinit var backupManager: BackupManager

    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var switchBackupReminder: SwitchMaterial
    private lateinit var tvCurrentCurrency: TextView
    private lateinit var tvBackupFrequency: TextView
    private lateinit var btnBackupData: MaterialButton
    private lateinit var btnRestoreData: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupListeners()
        loadCurrentSettings()
    }

    private fun initializeViews(view: View) {
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        switchBackupReminder = view.findViewById(R.id.switchBackupReminder)
        tvCurrentCurrency = view.findViewById(R.id.tvCurrentCurrency)
        tvBackupFrequency = view.findViewById(R.id.tvBackupFrequency)
        btnBackupData = view.findViewById(R.id.btnBackupData)
        btnRestoreData = view.findViewById(R.id.btnRestoreData)
    }

    private fun setupListeners() {
        // Dark Mode Switch
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setDarkMode(isChecked)
            updateTheme(isChecked)
        }

        // Notifications Switch
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setNotificationEnabled(isChecked)
            switchBackupReminder.isEnabled = isChecked
        }

        // Backup Reminder Switch
        switchBackupReminder.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setBackupReminderEnabled(isChecked)
        }

        // Currency Selection
        view?.findViewById<MaterialCardView>(R.id.cardCurrency)?.setOnClickListener {
            showCurrencySelector()
        }

        // Backup Frequency
        view?.findViewById<MaterialCardView>(R.id.cardBackupFrequency)?.setOnClickListener {
            showBackupFrequencyDialog()
        }

        // Backup Data
        btnBackupData.setOnClickListener {
            createBackup()
        }

        // Restore Data
        btnRestoreData.setOnClickListener {
            showRestoreConfirmationDialog()
        }

        // Reset Settings
        view?.findViewById<View>(R.id.btnResetSettings)?.setOnClickListener {
            showResetConfirmationDialog()
        }
    }

    private fun createBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = backupManager.createBackup()
                if (success) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(requireView(), "Data backed up successfully", Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(requireView(), "Backup failed", Snackbar.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(requireView(), "Backup failed: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showRestoreConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Restore Data")
            .setMessage("Are you sure you want to restore your data? This will replace all current transactions.")
            .setPositiveButton("Restore") { _, _ ->
                restoreFromBackup()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun restoreFromBackup() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = backupManager.restoreFromBackup()
                if (success) {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(requireView(), "Data restored successfully", Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(requireView(), "No backup data available to restore", Snackbar.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(requireView(), "Restore failed: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadCurrentSettings() {
        // Load Dark Mode
        switchDarkMode.isChecked = preferencesManager.isDarkModeEnabled()

        // Load Notifications
        switchNotifications.isChecked = preferencesManager.isNotificationEnabled()
        
        // Load Backup Settings
        switchBackupReminder.isChecked = preferencesManager.isBackupReminderEnabled()
        switchBackupReminder.isEnabled = preferencesManager.isNotificationEnabled()

        // Load Currency
        updateCurrencyDisplay()

        // Load Backup Frequency
        updateBackupFrequencyDisplay()
    }

    private fun showCurrencySelector() {
        val currencies = arrayOf("INR (₹)", "USD ($)", "EUR (€)", "GBP (£)")
        val currentCurrency = preferencesManager.getCurrency()
        val currentSelection = currencies.indexOfFirst { it.startsWith(currentCurrency) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Currency")
            .setSingleChoiceItems(currencies, currentSelection) { dialog, which ->
                val selected = currencies[which].substring(0, 3)
                preferencesManager.setCurrency(selected)
                updateCurrencyDisplay()
                dialog.dismiss()
            }
            .show()
    }

    private fun showBackupFrequencyDialog() {
        val frequencies = arrayOf("Every day", "Every 3 days", "Every 7 days", "Every 14 days", "Every 30 days")
        val currentFrequency = preferencesManager.getBackupFrequency()
        val currentSelection = when (currentFrequency) {
            1 -> 0
            3 -> 1
            7 -> 2
            14 -> 3
            30 -> 4
            else -> 2
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Backup Frequency")
            .setSingleChoiceItems(frequencies, currentSelection) { dialog, which ->
                val days = when (which) {
                    0 -> 1
                    1 -> 3
                    2 -> 7
                    3 -> 14
                    4 -> 30
                    else -> 7
                }
                preferencesManager.setBackupFrequency(days)
                updateBackupFrequencyDisplay()
                dialog.dismiss()
            }
            .show()
    }

    private fun showResetConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Reset Settings")
            .setMessage("Are you sure you want to reset all settings to default?")
            .setPositiveButton("Reset") { _, _ ->
                preferencesManager.clearAllPreferences()
                loadCurrentSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateCurrencyDisplay() {
        val currency = preferencesManager.getCurrency()
        val symbol = when (currency) {
            "INR" -> "₹"
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            else -> currency
        }
        tvCurrentCurrency.text = "$currency ($symbol)"
    }

    private fun updateBackupFrequencyDisplay() {
        val days = preferencesManager.getBackupFrequency()
        tvBackupFrequency.text = when (days) {
            1 -> "Every day"
            3 -> "Every 3 days"
            7 -> "Every 7 days"
            14 -> "Every 14 days"
            30 -> "Every 30 days"
            else -> "Every 7 days"
        }
    }

    private fun updateTheme(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        
        // Update navigation bar color
        activity?.let {
            it.window.navigationBarColor = if (isDarkMode) {
                ContextCompat.getColor(it, R.color.navigation_bar_dark)
            } else {
                ContextCompat.getColor(it, R.color.navigation_bar_light)
            }
        }
    }
} 