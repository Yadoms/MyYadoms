package com.yadoms.myyadoms.preferences

import LocationConverter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.commit
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.SelectKeywordPreferenceDialogFragment
import com.yadoms.myyadoms.checkLocationPermissions
import com.yadoms.myyadoms.location.LocationTrackingService
import com.yadoms.myyadoms.yadomsApi.ConfigurationApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi

class AwayFromHomeSettingsActivity : AppCompatActivity() { //TODO renommer la fonction en "AtHome" ?


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
        private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            awayFromHomeEnablePreference.isChecked =
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
                        && permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
                        && (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || permissions.getOrDefault(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    false
                ))

            awayFromHomePreferenceCategories.forEach {
                it.isEnabled = awayFromHomeEnablePreference.isChecked
            }

            startStopLocationTrackingService(awayFromHomeEnablePreference.isChecked)
        }
        private lateinit var preferences: SharedPreferences
        private lateinit var awayFromHomeEnablePreference: SwitchPreference
        private lateinit var awayFromHomePreferenceCategories: MutableList<Preference>
        private lateinit var awayFromHomeReferenceLocationPreference: ListPreference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.away_from_home_preferences, rootKey)

            //TODO le champ Direction est vraiment utile ?

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

            updateReferenceLocationSummary()

            awayFromHomeEnablePreference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean && !checkLocationPermissions(requireContext())) {
                    requestLocationPermissions()
                } else {
                    awayFromHomePreferenceCategories.forEach {
                        it.isEnabled = newValue
                    }
                    startStopLocationTrackingService(newValue)
                }
                true
            }

            awayFromHomeReferenceLocationPreference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == "YadomsServerLocation") getYadomsServerLocation() else getCurrentLocation()
                true
            }
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

        private fun setReferenceLocation(location: Location?) {
            if (location != null)
                preferences.edit {
                    putFloat("reference_location_latitude", location.latitude.toFloat())
                    putFloat("reference_location_longitude", location.longitude.toFloat())
                }

            updateReferenceLocationSummary()

            startStopLocationTrackingService(awayFromHomeEnablePreference.isChecked)

            if (location != null) {
                //TODO le onResumde l'activité principale ne suffit pas ?
//                val geofencingHelper = GeofencingHelper(requireContext())
//                geofencingHelper.addGeofence(
//                    location.latitude,
//                    location.longitude
//                )
            } else {
                //TODO le onResumde l'activité principale ne suffit pas ?
//                val geofencingHelper = GeofencingHelper(requireContext())
//                geofencingHelper.removeGeofence()

                Snackbar.make(
                    listView,
                    requireContext().getString(R.string.unable_to_retrieve_location),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        private fun updateReferenceLocationSummary() {
            val referenceLocation = Location("")
            referenceLocation.latitude = preferences.getFloat("reference_location_latitude", 0.0f).toDouble()
            referenceLocation.longitude = preferences.getFloat("reference_location_longitude", 0.0f).toDouble()

            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    referenceLocation.isComplete
                } else {
                    referenceLocation.latitude != 0.0 && referenceLocation.longitude != 0.0
                }
            ) {
                val converter = LocationConverter
                awayFromHomeReferenceLocationPreference.summary =
                    getString(
                        R.string.defined_location,
                        converter.latitudeAsDMS(referenceLocation.latitude, 10),
                        converter.longitudeAsDMS(referenceLocation.longitude, 10)
                    )
            } else {
                awayFromHomeReferenceLocationPreference.summary = ""
            }
        }

        private fun getYadomsServerLocation() {
            val yApi = YadomsApi(requireContext())
            ConfigurationApi(yApi).getYadomsServerLocation(
                onOk = ::setReferenceLocation,
                onError = { setReferenceLocation(null) }
            )
        }

        @SuppressLint("MissingPermission")
        private fun getCurrentLocation() {
            val locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager

            val providers = locationManager.getProviders(true)

            // Manage providers preferences : Network ==> GPS ==> others
            var location: Location? = null

            if (providers.contains(LocationManager.NETWORK_PROVIDER))
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location == null && providers.contains(LocationManager.GPS_PROVIDER))
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location == null) {
                for (provider in providers) {
                    location = locationManager.getLastKnownLocation(provider)
                    if (location != null)
                        break
                }
            }

            setReferenceLocation(location)
        }

        private fun requestLocationPermissions() {
            var permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                permissions += Manifest.permission.ACCESS_BACKGROUND_LOCATION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                permissions += Manifest.permission.FOREGROUND_SERVICE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                permissions += Manifest.permission.FOREGROUND_SERVICE_LOCATION

            requestPermissionLauncher.launch(permissions)
        }

        private fun startStopLocationTrackingService(awayFromHomeEnablePreferenceIsChecked: Boolean) {

            if (awayFromHomeEnablePreferenceIsChecked) {
                ContextCompat.startForegroundService(
                    requireContext(),
                    Intent(context, LocationTrackingService::class.java)
                )
            } else {
                val intent = Intent(context, LocationTrackingService::class.java).apply {
                    action = LocationTrackingService.ACTION_STOP
                }
                requireContext().startService(intent)
            }
        }
    }
}