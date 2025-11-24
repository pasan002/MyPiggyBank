package com.example.mypiggybank.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFERENCES_NAME = "mypiggybank_prefs"
        private const val KEY_FIRST_TIME_USER = "first_time_user"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_DAILY_REMINDER = "daily_reminder"
        private const val KEY_REMINDER_TIME = "reminder_time"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_BUDGET_ALERT_THRESHOLD = "budget_alert_threshold"
        private const val KEY_BACKUP_REMINDER_ENABLED = "backup_reminder_enabled"
        private const val KEY_BACKUP_FREQUENCY = "backup_frequency"
        private const val KEY_LAST_BACKUP_TIME = "last_backup_time"
    }


    // Currency
    fun setCurrency(currency: String) {
        sharedPreferences.edit().putString(KEY_CURRENCY, currency).apply()
    }

    fun getCurrency(): String {
        return sharedPreferences.getString(KEY_CURRENCY, "INR") ?: "INR"
    }

    // Dark Mode
    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    // Notifications
    fun setNotificationEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }

    fun isNotificationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    }

    fun isDailyReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DAILY_REMINDER, false)
    }

    // Language
    fun setLanguage(languageCode: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    // Backup Settings
    fun setBackupReminderEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BACKUP_REMINDER_ENABLED, enabled).apply()
    }

    fun isBackupReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BACKUP_REMINDER_ENABLED, true)
    }

    fun setBackupFrequency(days: Int) {
        sharedPreferences.edit().putInt(KEY_BACKUP_FREQUENCY, days).apply()
    }

    fun getBackupFrequency(): Int {
        return sharedPreferences.getInt(KEY_BACKUP_FREQUENCY, 7) // Default 7 days
    }

    fun setLastBackupTime(timestamp: Long) {
        sharedPreferences.edit().putLong(KEY_LAST_BACKUP_TIME, timestamp).apply()
    }

    fun getLastBackupTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_BACKUP_TIME, 0)
    }

    // Clear all preferences
    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}
