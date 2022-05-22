package com.elnemr.runningtracker.presentation.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
import com.elnemr.runningtracker.presentation.services.polyLine
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import pub.devrel.easypermissions.EasyPermissions


object LocationUtils {

    const val PERMISSION_REQUEST_ACCESS_LOCATION = 100


    fun moveCameraToUserLocation(latestPolyline: polyLine, map: GoogleMap?) {
        if (latestPolyline.isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latestPolyline.last(), Constants.MAP_ZOOM)
            )
        }
    }

    fun addAllPolyLines(pathPoints: MutableList<polyLine>, map: GoogleMap?) {
        for (polyLine in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .addAll(polyLine)

            map?.addPolyline(polylineOptions)
        }
    }

    fun addLatestPolyline(latestPolyline: polyLine, map: GoogleMap?) {
        if (latestPolyline.size > 1) {
            val preLastLatLng = latestPolyline[latestPolyline.size - 2]
            val lastLatLng = latestPolyline.last()

            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)

        }
    }


    fun hasLocationPermissions(context: Context): Boolean =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun isLocationEnabled(context: Context): Boolean {
        var gpsEnabled = false
        var networkEnabled = false

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            networkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (exception: Exception) {
        }

        return gpsEnabled && networkEnabled

    }

    fun showSettingsAlert(context: Context) {

        val intent = Intent(
            Settings.ACTION_LOCATION_SOURCE_SETTINGS
        )
        startActivity(context, intent, null)

    }

}