package com.yadoms.myyadoms

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofencingHelper(private val context: Context) {
    private val _logTag = javaClass.canonicalName

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    companion object {
        private const val GEOFENCE_RADIUS_IN_METERS = 100f
        private const val GEOFENCE_ID = "MyYadoms.awayFromHomeGeoFencing"
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(latitude: Double, longitude: Double) {
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS_IN_METERS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT or GeofencingRequest.INITIAL_TRIGGER_DWELL)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
            .addOnSuccessListener { Log.i(_logTag, "$GEOFENCE_ID geofence ($latitude, $longitude) added") }
            .addOnFailureListener { Log.e(_logTag, "Error adding $GEOFENCE_ID geofence ($latitude, $longitude) : ${it.message}") }
    }

    @SuppressLint("MissingPermission")
    fun removeGeofence() {
        geofencingClient.removeGeofences(listOf(GEOFENCE_ID))
    }

//    private fun getGeofencePendingIntent(): PendingIntent {
//        Log.d(_logTag, "create getGeofencePendingIntent")//TODO virer
//        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
//        return PendingIntent.getBroadcast(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//    }

    private fun getGeofencePendingIntent(): PendingIntent {
        Log.d(_logTag, "create getGeofencePendingIntent")//TODO virer
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = "com.yadoms.myyadoms.ACTION_GEOFENCE_TRANSITION" // Define a unique action
        }
        return PendingIntent.getBroadcast(
            context,
            168,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
