package com.elnemr.runningtracker.presentation.util

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import javax.inject.Inject

// Google’s Location Services API
@SuppressLint("MissingPermission")
class LocationHelper @Inject constructor(private val fusedLocationProviderClient: FusedLocationProviderClient) {

    private lateinit var locationCallback: LocationCallback

    fun startTracking(listener: ILocationListener) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locations: LocationResult) {
                super.onLocationResult(locations)
                for (location in locations.locations) {
                    listener.onLocationResult(location)
                }
            }
        }

        val locationRequest = LocationRequest.create().apply {
            fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
            interval = Constants.LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopTracking() {
        if (this::locationCallback.isInitialized) fusedLocationProviderClient.removeLocationUpdates(
            locationCallback
        )
    }

    interface ILocationListener {
        fun onLocationResult(location: Location)
    }
}