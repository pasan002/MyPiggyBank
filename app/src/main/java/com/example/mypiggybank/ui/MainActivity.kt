package com.example.mypiggybank.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mypiggybank.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatDelegate
import android.view.WindowManager
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        // Configure the top-level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_transactions,
                R.id.navigation_budget,
                R.id.navigation_tools,
                R.id.navigation_settings
            )
        )

        // Set up bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Update navigation bar color based on theme
        updateNavigationBarColor()

        // Listen for theme changes
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun isDarkModeEnabled(): Boolean {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
        }
    }

    private fun updateNavigationBarColor() {
        val isDarkMode = isDarkModeEnabled()
        window.navigationBarColor = if (isDarkMode) {
            ContextCompat.getColor(this, R.color.navigation_bar_dark)
        } else {
            ContextCompat.getColor(this, R.color.navigation_bar_light)
        }
    }

    override fun onResume() {
        super.onResume()
        updateNavigationBarColor()
    }
}
