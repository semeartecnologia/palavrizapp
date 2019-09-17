package com.palavrizar.tec.palavrizapp.utils.commons

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import com.palavrizar.tec.palavrizapp.models.Location

class DeviceInfoHelper(val context: Context) {

    fun getCurrentLocation(): Location? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) return null
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: android.location.Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return Location(bestLocation?.latitude?.toFloat() ?: 0f, bestLocation?.longitude?.toFloat()
                ?: 0f)
    }
}