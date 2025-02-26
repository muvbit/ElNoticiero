package com.muvbit.elnoticiero.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.muvbit.elnoticiero.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}