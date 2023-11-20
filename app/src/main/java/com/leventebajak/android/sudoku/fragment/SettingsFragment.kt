package com.leventebajak.android.sudoku.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.leventebajak.android.sudoku.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_settingsFragment_to_menuFragment)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>("back_to_menu")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_menuFragment)
            true
        }

        findPreference<Preference>("email")?.setOnPreferenceClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_SENDTO,
                    Uri.parse("mailto:${getString(R.string.email_address)}")
                )
            )
            true
        }

        findPreference<Preference>("github")?.setOnPreferenceClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/${getString(R.string.github_username)}")
                )
            )
            true
        }
    }
}