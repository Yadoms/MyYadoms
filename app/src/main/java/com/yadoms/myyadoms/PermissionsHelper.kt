package com.yadoms.myyadoms

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun checkLocationPermissions(context:android.content.Context): Boolean {
    return (ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)
}