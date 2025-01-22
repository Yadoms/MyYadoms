package com.yadoms.myyadoms.awayFromHome

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.yadoms.myyadoms.NewWidgetActivityContract
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.databinding.ActivityAwayFromHomeBinding
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.widgets.WidgetTypes
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi


class AwayFromHomeActivity : AppCompatActivity() {
    private val _logTag = javaClass.canonicalName
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityAwayFromHomeBinding
    private var entering: Boolean = true

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAwayFromHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        with(binding) {
            awayFromHomeEnable.setOnCheckedChangeListener { _, isChecked ->
                enableLayout(conditionLayout, isChecked)
                enableLayout(actionLayout, isChecked)
            }
            referenceLocation.setOnClickListener {
                checkLocationPermissions()
                fusedLocationClient!!.lastLocation
                    .addOnSuccessListener()
                    { location ->
                        if (location != null) {
                            val currentLocation = findViewById<TextView>(R.id.selected_reference_location)
                            currentLocation.text = getString(R.string.defined_location, location.latitude, location.longitude)
                        } else {
                            Snackbar.make(
                                view,
                                applicationContext.getString(R.string.unable_to_retrieve_location),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }

            val locationDirectionsStrings = getResources().getStringArray(R.array.entering_or_leaving)
            locationDirectionValue.text = locationDirectionsStrings[if (entering) 0 else 1]

            locationDirection.setOnClickListener {
                AlertDialog.Builder(this@AwayFromHomeActivity)
                    .setTitle(R.string.location_direction_title)
                    .setItems(locationDirectionsStrings) { _, selected ->
                        entering = selected == 0
                        locationDirectionValue.text = locationDirectionsStrings[selected]
                    }
                    .show()
            }

            val newWidgetActivityContractLauncher =
                registerForActivityResult(
                    NewWidgetActivityContract(
                        getString(R.string.select_keyword_to_drive),
                        arrayOf(WidgetTypes.WidgetType.Switch),
                        false
                    )
                ) { selectedData: Preferences.WidgetData? ->
                    selectedData?.let { Log.d(_logTag, "Selected keyword ID = ${selectedData.keywordId}") }
                    val keywordToControl = findViewById<TextView>(R.id.device_to_control_value)
                    if (selectedData == null) {
                        keywordToControl.text = getString(R.string.no_data)
                    } else {
                        DeviceApi(YadomsApi(baseContext)).getKeyword(
                            selectedData.keywordId,
                            onOk = {
                                keywordToControl.text = it.friendlyName
                            },
                            onError = {
                                keywordToControl.text = java.lang.String("${selectedData.keywordId}")
                            })
                    }
                }
            deviceToControl.setOnClickListener {
                newWidgetActivityContractLauncher.launch(Unit)
            }
        }
    }

    private fun enableLayout(layout: android.view.ViewGroup, enable: Boolean) {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            child.isEnabled = enable
        }
        layout.alpha = if (enable) 1.0f else 0.3f
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

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
            return

        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                }

                else -> {
                    finish()
                }
            }
        }.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}