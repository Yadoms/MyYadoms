package com.yadoms.myyadoms.preferences

import LocationConverter
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.takisoft.preferencex.PreferenceFragmentCompat
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.yadomsApi.ConfigurationApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi

class AwayFromHomeSettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.away_from_home_settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.away_from_home_settings, AwayFromHomeSettingsFragment()).commit()
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
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            awayFromHomeEnablePreference.isChecked =
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
                        || permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

            awayFromHomePreferenceCategories.forEach {
                it.isEnabled = awayFromHomeEnablePreference.isChecked
            }
        }
        private lateinit var awayFromHomeEnablePreference: SwitchPreference
        private lateinit var awayFromHomePreferenceCategories: MutableList<Preference>
        private lateinit var awayFromHomeReferenceLocationPreference: ListPreference

        override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.away_from_home_preferences, rootKey)

            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val awayFromHomeEnablePreferenceKey = "away_from_home_enable"
            awayFromHomeEnablePreference = findPreference(awayFromHomeEnablePreferenceKey)!!
            awayFromHomePreferenceCategories = arrayListOf(
                findPreference("reference_location_category")!!,
                findPreference("device_to_control_category")!!
            )
            awayFromHomeReferenceLocationPreference = findPreference("reference_location")!!

            val hasLocationPermissions = checkLocationPermissions()

            if (!hasLocationPermissions && preferences.getBoolean(awayFromHomeEnablePreferenceKey, false))
                awayFromHomeEnablePreference.isChecked = false

            awayFromHomePreferenceCategories.forEach {
                it.isEnabled = awayFromHomeEnablePreference.isChecked
            }

            awayFromHomeEnablePreference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean && !checkLocationPermissions())
                    requestLocationPermissions()
                else
                    awayFromHomePreferenceCategories.forEach {
                        it.isEnabled = newValue
                    }

                true
            }

            awayFromHomeReferenceLocationPreference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == "YadomsServerPosition") getYadomsServerPosition() else getCurrentPosition()
                true
            }

            if (hasLocationPermissions)
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        }

        @SuppressLint("MissingPermission")
        private fun getCurrentPosition() {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(::updateLocation)
        }

        private fun updateLocation(location: Location?) {
            if (location != null) {
                val converter = LocationConverter
                awayFromHomeReferenceLocationPreference.summary =
                    getString(
                        R.string.defined_location,
                        converter.latitudeAsDMS(location.latitude, 10),
                        converter.longitudeAsDMS(location.longitude, 10)
                    )
            } else {
                Snackbar.make(
                    listView,
                    requireContext().getString(R.string.unable_to_retrieve_location),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        private fun getYadomsServerPosition() {
            val yApi = YadomsApi(requireContext())
            ConfigurationApi(yApi).getYadomsServerPosition(
                onOk = ::updateLocation,
                onError = {updateLocation(null)}
            )
        }

        private fun checkLocationPermissions(): Boolean {
            return (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        }

        private fun requestLocationPermissions() {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}