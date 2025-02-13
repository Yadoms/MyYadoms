package com.yadoms.myyadoms;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.Geofence

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val _logTag = javaClass.canonicalName
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(_logTag, "intent received : ${intent.action}")//TODO virer
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            Log.e(_logTag, "Geofencing error : ${geofencingEvent.errorCode}")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(_logTag, "User entered into geofence")
        }
        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(_logTag, "User exited from geofence")
        }
    }
}

