package com.yadoms.myyadoms

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun checkLocationPermissions(context: android.content.Context): Boolean {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        return false

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        return false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    )
        return false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.FOREGROUND_SERVICE
        ) != PackageManager.PERMISSION_GRANTED
    )
        return false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    )
        return false

    return true
}