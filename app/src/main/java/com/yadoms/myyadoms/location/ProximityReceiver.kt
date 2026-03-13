package com.yadoms.myyadoms.location
//TODO tout relire

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import android.widget.Toast

class ProximityReceiver : BroadcastReceiver() {
    private val logTag = this.javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(
            logTag,
            "onReceive action=${intent.action} extras=${intent.extras}"
        )

        if (intent.action != "com.yadoms.myyadoms.location.PROXIMITY_ALERT")
            return

        val entering = intent.getBooleanExtra(
            LocationManager.KEY_PROXIMITY_ENTERING,
            false
        )

        if (entering) {
            Log.d(logTag, "Entrée dans la zone !")
            Toast.makeText(context, "Entrée dans la zone", Toast.LENGTH_LONG).show()
        } else {
            Log.d(logTag, "Sortie de la zone !")
            Toast.makeText(context, "Sortie de la zone", Toast.LENGTH_LONG).show()
        }
    }
}