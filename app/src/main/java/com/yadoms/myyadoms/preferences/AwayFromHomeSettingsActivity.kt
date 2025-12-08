package com.yadoms.myyadoms.preferences

import LocationConverter
import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.yadoms.myyadoms.GeofencingHelper
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.SelectKeywordPreferenceDialogFragment
import com.yadoms.myyadoms.checkLocationPermissions
import com.yadoms.myyadoms.yadomsApi.ConfigurationApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi
import androidx.core.content.edit

class AwayFromHomeSettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.away_from_home_settings_activity)

        // TODO les 2 instructions sont utiles ?
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.away_from_home_settings, AwayFromHomeSettingsFragment()).commit()
        }
        supportFragmentManager.commit {
            replace(R.id.away_from_home_settings, AwayFromHomeSettingsFragment())
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

    class AwayFromHomeSettingsFragment : PreferenceFragmentCompat(), PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {
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
        private lateinit var preferences: SharedPreferences
        private lateinit var awayFromHomeEnablePreference: SwitchPreference
        private lateinit var awayFromHomePreferenceCategories: MutableList<Preference>
        private lateinit var awayFromHomeReferenceLocationPreference: ListPreference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.away_from_home_preferences, rootKey)

            preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val awayFromHomeEnablePreferenceKey = "away_from_home_enable"
            awayFromHomeEnablePreference = findPreference(awayFromHomeEnablePreferenceKey)!!
            awayFromHomePreferenceCategories = arrayListOf(
                findPreference("reference_location_category")!!,
                findPreference("device_to_control_category")!!
            )
            awayFromHomeReferenceLocationPreference = findPreference("reference_location_choice")!!

            val hasLocationPermissions = checkLocationPermissions(requireContext())

            if (!hasLocationPermissions && preferences.getBoolean(awayFromHomeEnablePreferenceKey, false))
                awayFromHomeEnablePreference.isChecked = false

            awayFromHomePreferenceCategories.forEach {
                it.isEnabled = awayFromHomeEnablePreference.isChecked
            }

            awayFromHomeEnablePreference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean && !checkLocationPermissions(requireContext()))
                    requestLocationPermissions()
                else
                    awayFromHomePreferenceCategories.forEach {
                        it.isEnabled = newValue
                    }

                true
            }

            awayFromHomeReferenceLocationPreference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == "YadomsServerLocation") getYadomsServerLocation() else getCurrentLocation()
                true
            }

            if (hasLocationPermissions)
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        }

        override fun onPreferenceDisplayDialog(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
            if (pref is SelectKeywordPreference) {
                val dialogFragment = SelectKeywordPreferenceDialogFragment.newInstance(pref.key)
                dialogFragment.setTargetFragment(caller, 0) //TODO revoir
                dialogFragment.show(parentFragmentManager, "SelectKeywordPreference")
                return true
            }
            return false
        }

        @SuppressLint("MissingPermission")
        private fun getCurrentLocation() {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(::updateLocation)
        }

        private fun updateLocation(location: Location?) {
            preferences.edit {
                putString("reference_location", location?.toString() ?: "")
            }

            if (location != null) {
                val converter = LocationConverter
                awayFromHomeReferenceLocationPreference.summary =
                    getString(
                        R.string.defined_location,
                        converter.latitudeAsDMS(location.latitude, 10),
                        converter.longitudeAsDMS(location.longitude, 10)
                    )

                val geofencingHelper = GeofencingHelper(requireContext())
                geofencingHelper.addGeofence(
                    location.latitude,
                    location.longitude
                )
            } else {
                awayFromHomeReferenceLocationPreference.summary = ""

                val geofencingHelper = GeofencingHelper(requireContext())
                geofencingHelper.removeGeofence()

                Snackbar.make(
                    listView,
                    requireContext().getString(R.string.unable_to_retrieve_location),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        private fun getYadomsServerLocation() {
            val yApi = YadomsApi(requireContext())
            ConfigurationApi(yApi).getYadomsServerLocation(
                onOk = ::updateLocation,
                onError = { updateLocation(null) }
            )
        }

        private fun requestLocationPermissions() {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }
}