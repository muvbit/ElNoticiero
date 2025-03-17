package com.muvbit.elnoticiero.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val activity = requireActivity() as MainActivity
        activity.binding.bottomNav.menu.clear()
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}