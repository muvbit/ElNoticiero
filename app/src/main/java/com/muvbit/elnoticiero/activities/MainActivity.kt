package com.muvbit.elnoticiero.activities

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.ActivityMainBinding
import com.muvbit.elnoticiero.databinding.NavHeaderBinding
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.mainDrawer
        navigationView = binding.navigation

        // Get the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        // Get the NavController
        navController = navHostFragment.navController



        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.firstFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_favorites -> {
                    navController.navigate(R.id.favoriteNewsFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.settingsFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }
        updateNavHeader()


        binding.drawerToggle.setOnClickListener {
            openDrawer()
        }

    }// Close onCreate

    // Handle back button presses
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun updateNavHeader(){
        val navHeader=navigationView.getHeaderView(0)
        val navHeaderBinding : NavHeaderBinding = NavHeaderBinding.bind(navHeader)
        navHeaderBinding.navDate.text=getCurrentDate()
    }

    private fun getCurrentDate(): String {
        val calendar = GregorianCalendar()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        return formattedDate
    }
    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}