package com.muvbit.elnoticiero.activities
import android.os.Bundle
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
import com.muvbit.elnoticiero.fragments.radio.RadioPlayerFragment
import com.muvbit.elnoticiero.player.AudioPlayerManager.stop
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


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navController = navHostFragment.navController
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.firstFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_tv -> {
                    navController.navigate(R.id.tvFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_radio -> {
                    navController.navigate(R.id.radioFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_news -> {
                    navController.navigate(R.id.mainFragment)
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
        updateNavHeader()


        binding.drawerToggle.setOnClickListener {
            openDrawer()
        }

    }


    // Handle back button presses
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun updateNavHeader(){
        val navHeader=navigationView.getHeaderView(0)
        val navHeaderBinding : NavHeaderBinding = NavHeaderBinding.bind(navHeader)
        navHeaderBinding.navDate.text=getCurrentDate()
        val pref= PreferenceManager.getDefaultSharedPreferences(this)
        navHeaderBinding.helloUser.text = pref.getString("userName", getString(R.string.default_user_name)) ?: getString(R.string.default_user_name)
    }

    private fun getCurrentDate(): String {
        val calendar = GregorianCalendar()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        return formattedDate
    }
    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
        updateNavHeader()
    }
}