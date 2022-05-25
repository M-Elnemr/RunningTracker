package com.elnemr.runningtracker.presentation.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit


object LocationUtils {

    const val PERMISSION_REQUEST_ACCESS_LOCATION = 100

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {

        var milliseconds = ms
//        val hours: Int = (milliseconds / (1000 * 60 * 60) ).toInt()
//        milliseconds %= (1000 * 60 * 60)
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 100
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "$milliseconds"

    }

    fun calculatePolylineLength(polyLine: polyLine): Float{
        var distance = 0f
        for (i in 0..polyLine.size -2){
            val pos1 = polyLine[i]
            val pos2 = polyLine[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude, result
            )

            distance += result[0]
        }
        return distance
    }

    fun zoomToSeeWholeTrack(pathPoints: MutableList<polyLine>, map: GoogleMap?, mapView: MapView) {
        val bounds = LatLngBounds.builder()
        for (polyLine in pathPoints) {
            for (pos in polyLine) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(), mapView.height, mapView.width, (mapView.height * 0.05f).toInt()
            )
        )
    }

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