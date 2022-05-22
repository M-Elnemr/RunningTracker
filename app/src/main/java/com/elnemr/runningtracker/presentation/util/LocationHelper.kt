package com.elnemr.runningtracker.presentation.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

// Google’s Location Services API
@SuppressLint("MissingPermission")
class LocationHelper() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback

    fun startTracking(context: Context, listener: ILocationListener){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

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
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopTracking() = fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    interface ILocationListener {
        fun onLocationResult(location: Location)
    }
}