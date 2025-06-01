package com.muvbit.elnoticiero.activities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.ActivityMainBinding
import com.muvbit.elnoticiero.databinding.NavHeaderBinding
import com.muvbit.elnoticiero.fragments.FirstFragment
import com.muvbit.elnoticiero.fragments.SettingsFragment
import com.muvbit.elnoticiero.fragments.news.FavoriteNewsFragment
import com.muvbit.elnoticiero.fragments.news.NewsFragment
import com.muvbit.elnoticiero.fragments.radio.RadioFragment
import com.muvbit.elnoticiero.fragments.radio.RadioPlayerFragment
import com.muvbit.elnoticiero.fragments.tv.TvFragment
import com.muvbit.elnoticiero.player.AudioPlayerManager.stop
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale
import com.muvbit.elnoticiero.BuildConfig
import com.muvbit.elnoticiero.databinding.NavFooterBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    var shouldShowBottomNav = true


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.mainDrawer
        navigationView = binding.navigation

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navController = navHostFragment.navController
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (navHostFragment.childFragmentManager.primaryNavigationFragment is FirstFragment) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else{
                        navController.navigate(R.id.firstFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.nav_tv -> {
                    if (navHostFragment.childFragmentManager.primaryNavigationFragment is TvFragment) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else{
                        navController.navigate(R.id.tvFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.nav_radio -> {
                    if (navHostFragment.childFragmentManager.primaryNavigationFragment is RadioFragment) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }else{
                        navController.navigate(R.id.radioFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.nav_news -> {
                    if (navHostFragment.childFragmentManager.primaryNavigationFragment is NewsFragment) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else{
                        navController.navigate(R.id.mainFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }

                R.id.nav_favorites -> {
                    if (navHostFragment.childFragmentManager.primaryNavigationFragment is FavoriteNewsFragment) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else{
                        navController.navigate(R.id.favoriteNewsFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.nav_settings -> {
                    if (navHostFragment.childFragmentManager.primaryNavigationFragment is SettingsFragment) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }else{
                        navController.navigate(R.id.settingsFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    true
                }

                R.id.nav_stop -> {
                    (navHostFragment.childFragmentManager.primaryNavigationFragment as? RadioPlayerFragment)?.apply {
                        stop()
                        if (!findNavController().popBackStack()) {
                            requireActivity().finish()
                        }
                    } ?: run {
                        stop()
                    }

                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }

        }
        updateNavHeaderAndFooter()

        binding.drawerToggle.setOnClickListener {
            openDrawer()
        }

    }

    fun hideBottomNavigation() {
        shouldShowBottomNav = false
        binding.bottomAppBar.animate()
            .translationY(binding.bottomAppBar.height.toFloat())
            .setDuration(300)
            .withEndAction {
                binding.bottomAppBar.visibility = View.GONE
            }
            .start()
        binding.drawerToggle.hide()
    }

    fun showBottomNavigation() {
        shouldShowBottomNav = true
        binding.bottomAppBar.visibility = View.VISIBLE
        binding.bottomAppBar.animate()
            .translationY(0f)
            .setDuration(300)
            .start()
        binding.drawerToggle.show()
    }
    fun updateBottomNavigationVisibility() {
        if (shouldShowBottomNav) {
            showBottomNavigation()
        } else {
            hideBottomNavigation()
        }
    }


    // Handle back button presses
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun updateNavHeaderAndFooter(){
        val navHeader=navigationView.getHeaderView(0)
        val navHeaderBinding : NavHeaderBinding = NavHeaderBinding.bind(navHeader)
        navHeaderBinding.navDate.text=getCurrentDate()
        val pref= PreferenceManager.getDefaultSharedPreferences(this)
        navHeaderBinding.helloUser.text = pref.getString("userName", getString(R.string.default_user_name)) ?: getString(R.string.default_user_name)
        findViewById<TextView>(R.id.tvBuildNumVersion).text=BuildConfig.VERSION_NAME

    }

    private fun getCurrentDate(): String {
        val calendar = GregorianCalendar()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        return formattedDate
    }
    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
        updateNavHeaderAndFooter()
    }
}