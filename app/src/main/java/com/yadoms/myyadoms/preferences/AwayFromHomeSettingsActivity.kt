package com.yadoms.myyadoms.preferences

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.takisoft.preferencex.PreferenceFragmentCompat
import com.yadoms.myyadoms.R

class AwayFromHomeSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.away_from_home_settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.away_from_home_settings, AwayFromHomeSettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class AwayFromHomeSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.away_from_home_preferences, rootKey)

            enablePreferencesFrom(
                "away_from_home_enable",
                arrayOf(
                    "reference_location_category",
                    "device_to_control"
                )
            )

//            enablePreferencesFrom(
//                "server_use_basic_authentication",
//                arrayOf(
//                    "server_basic_authentication_username",
//                    "server_basic_authentication_password"
//                )
//            )
        }

        private fun enablePreferencesFrom(masterKey: String, slaveKeys: Array<String>) {
            val masterPreference = findPreference<Preference>(masterKey)

            val slavePreferences: MutableList<Preference?> = ArrayList()
            slaveKeys.forEach { slavePreferences.add(findPreference(it)) }

            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            if (preferences.getBoolean(masterKey, false)) {
                slavePreferences.forEach { it?.isEnabled = true }
            } else {
                slavePreferences.forEach { it?.isEnabled = false }
            }

            masterPreference?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) {
                    slavePreferences.forEach { it?.isEnabled = true }
                } else {
                    slavePreferences.forEach { it?.isEnabled = false }
                }
                true
            }
        }
    }
}