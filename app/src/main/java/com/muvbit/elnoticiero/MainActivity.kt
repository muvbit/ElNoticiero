package com.muvbit.elnoticiero

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.muvbit.elnoticiero.databinding.ActivityMainBinding

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

        drawerLayout=binding.mainDrawer
        navigationView=binding.navigation

        // Get the NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        // Get the NavController
        navController = navHostFragment.navController

        navigationView.setNavigationItemSelectedListener {
            menuItem -> when (menuItem.itemId) {
                    R.id.nav_home -> {
                        navController.navigate(R.id.mainFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }
                    else -> false
                }
            }


        binding.drawerToggle.setOnClickListener{
            openDrawer()
        }

        }// Close onCreate

    // Handle back button presses
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}