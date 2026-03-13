package com.yadoms.myyadoms.location

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission
import com.yadoms.myyadoms.preferences.Preferences

class ProximityAlert {
    companion object {
        private val logTag = this::class.java.canonicalName
        @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
        fun register(context: Context) {

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val awayFromHomePreferences = Preferences(context).awayFromHome

            val expiration = -1L // -1 = jamais d’expiration //TODO à voir

            val intent = Intent(context, ProximityReceiver::class.java).apply {
                action = "com.yadoms.myyadoms.location.PROXIMITY_ALERT" //TODO privatiser l'intent
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.d(logTag,
                "Adding proximity alert : ${awayFromHomePreferences.referenceLocation.latitude}, ${awayFromHomePreferences.referenceLocation.longitude}, ${awayFromHomePreferences.referenceLocationDistance.toFloat()}m")

            locationManager.addProximityAlert(
                awayFromHomePreferences.referenceLocation.latitude,
                awayFromHomePreferences.referenceLocation.longitude,
                awayFromHomePreferences.referenceLocationDistance.toFloat(),
                expiration,
                pendingIntent
            )
        }

        fun remove(context: Context) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val intent = Intent(context, ProximityReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.d(logTag,
                "Remove proximity alert")

            locationManager.removeProximityAlert(pendingIntent)
        }
    }
}