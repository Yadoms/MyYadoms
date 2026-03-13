package com.yadoms.myyadoms.location

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.yadoms.myyadoms.BuildConfig
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationTrackingService : Service() { //TODO virer ?
    private val logTag = this.javaClass.simpleName

    private lateinit var locationManager: LocationManager
    private lateinit var handlerThread: HandlerThread
    private lateinit var handler: Handler

    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    private var near = NotificationType.AtHome

    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            CoroutineScope(Dispatchers.Default).launch {

                val awayFromHomePreferences = Preferences(application).awayFromHome

                var distance = awayFromHomePreferences.referenceLocation.distanceTo(location)

                val nearThreshold = awayFromHomePreferences.referenceLocationDistance
                var farThreshold = when {
                    nearThreshold <= 300 -> nearThreshold * 5
                    else -> nearThreshold * 3
                }

                val newNear = when {
                    (distance < nearThreshold) -> NotificationType.AtHome
                    (distance < farThreshold) -> NotificationType.Near
                    else -> NotificationType.Far
                }

                Log.i(
                    logTag,
                    location.toString()
                            + ", distance = " + distance
                            + ", near = " + newNear
                )

                updateNotification(near)

                if (newNear != near) {
                    activateDevice(
                        awayFromHomePreferences.device,
                        newNear == NotificationType.AtHome
                    )
                    near = newNear
                    updateLocationsGettingRules()
                }
            }
        }

        //TODO écouter les changements de config (onProviderEnabled/onProviderDisabled
    }

    private fun activateDevice(device: Int, on: Boolean) {
        DeviceApi(YadomsApi(application.applicationContext)).command(
            device, //TODO renommer en keyword mais s'assurer que c'est bien un keyword qui est configuré
            if (on) "1" else "0",
            {
                //TODO MAJ la notification
            }) {}
    }

    private fun updateNotification(type: NotificationType) {
        val updated = buildNotification(type)
        notificationManager.notify(NOTIF_ID, updated)
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        startForeground(NOTIF_ID, buildNotification(near))

        handlerThread = HandlerThread("LocationThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationsGettingRules() {
        val provider = selectBestProvider()

        if (provider == null) {
            updateNotification(NotificationType.NoLocation)
            return
        }

        locationManager.removeUpdates(listener)

        var minTimeMs = 30_000L
        var minDistanceM = 500f
        when (near) {
            NotificationType.AtHome -> {
                minTimeMs = 10_000L
                minDistanceM = 20f
            }

            NotificationType.Near -> {
                minTimeMs = 5_000L
                minDistanceM = 5f
            }

            NotificationType.Far -> {
                minTimeMs = 20_000L
                minDistanceM = 200f
            }

            else -> {}
        }

        Log.i(
            logTag,
            "Update location request : provider = " + provider +
                    ", minTimeMs = " + minTimeMs +
                    ", minDistanceM = " + minDistanceM
        )
        locationManager.requestLocationUpdates(
            provider,
            minTimeMs,
            minDistanceM,
            listener,
            handlerThread.looper
        )
    }

    @SuppressLint("InlinedApi")
    private fun selectBestProvider(): String? {
        val providers = locationManager.getProviders(true)

        return if (near == NotificationType.AtHome && !BuildConfig.DEBUG)
            when {
                LocationManager.FUSED_PROVIDER in providers -> LocationManager.FUSED_PROVIDER
                LocationManager.NETWORK_PROVIDER in providers -> LocationManager.NETWORK_PROVIDER
                LocationManager.GPS_PROVIDER in providers -> LocationManager.GPS_PROVIDER
                else -> null
            }
        else
            when {
                LocationManager.FUSED_PROVIDER in providers -> LocationManager.FUSED_PROVIDER
                LocationManager.GPS_PROVIDER in providers -> LocationManager.GPS_PROVIDER
                LocationManager.NETWORK_PROVIDER in providers -> LocationManager.NETWORK_PROVIDER
                else -> null
            }
    }

    private fun startLocationUpdates() {
        handler.post {
            updateLocationsGettingRules()
        }
    }

    override fun onDestroy() {
        locationManager.removeUpdates(listener)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private enum class NotificationType(@param:StringRes val labelResId: Int) {
        NoLocation(R.string.away_from_home_no_location),
        AtHome(R.string.away_from_home_state_at_home),
        Near(R.string.away_from_home_state_near),
        Far(R.string.away_from_home_state_far)
    }

    private fun buildNotification(type: NotificationType): Notification {
        val channelId = "location_tracking"

        val channel = NotificationChannel(
            channelId,
            getString(R.string.away_from_home),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.away_from_home))
            .setContentText(getString(type.labelResId))
            .setSmallIcon(R.drawable.ic_application_logo)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val NOTIF_ID = 1
        const val ACTION_STOP = "com.yadoms.myyadoms.location.LocationTrackingService.ACTION_STOP"
    }
}
