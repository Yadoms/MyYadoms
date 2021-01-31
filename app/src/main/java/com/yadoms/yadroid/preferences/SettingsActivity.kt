package com.yadoms.yadroid.preferences

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import com.takisoft.preferencex.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.yadoms.yadroid.R
import com.yadoms.yadroid.ScrollingActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
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
                val intent = Intent(this@SettingsActivity, ScrollingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            enablePreferencesFrom(
                "server_use_https",
                arrayOf(
                    "server_https_port"
                )
            )

            enablePreferencesFrom(
                "server_use_basic_authentication",
                arrayOf(
                    "server_basic_authentication_username",
                    "server_basic_authentication_password"
                )
            )
        }

        private fun enablePreferencesFrom(masterKey: String, slaveKeys: Array<String>) {
            val masterPreference = findPreference<Preference>(masterKey)

            val slavePreferences: MutableList<Preference?> = ArrayList()
            slaveKeys.forEach { slavePreferences.add(findPreference(it)) }

            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            if (preferences.getBoolean(masterKey, false)) {
                slavePreferences.forEach { it?.isEnabled = true }
            } else {
                slavePreferences.forEach { it?.isEnabled = false }
            }

            masterPreference?.setOnPreferenceChangeListener { preference, newValue ->
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