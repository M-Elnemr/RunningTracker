package com.elnemr.runningtracker.presentation.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.presentation.ui.main.MainActivity
import com.elnemr.runningtracker.presentation.util.Constants
import com.elnemr.runningtracker.presentation.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.elnemr.runningtracker.presentation.util.Constants.FASTEST_LOCATION_INTERVAL
import com.elnemr.runningtracker.presentation.util.Constants.LOCATION_UPDATE_INTERVAL
import com.elnemr.runningtracker.presentation.util.Constants.TRACKING_NOTIFICATION_ID
import com.elnemr.runningtracker.presentation.util.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

typealias polyLine = MutableList<LatLng>
typealias polyLines = MutableList<polyLine>

// LifecycleService contains LifecycleOwner for observing
class TrackingService : LifecycleService() {

    private var isFirstRun = true

    //5
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val pathPoints: MutableStateFlow<polyLines> = MutableStateFlow(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        CoroutineScope(Dispatchers.Default).launch {
            isTracking.collect {
                updateLocationTracking(it)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForeGroundService()
                        isFirstRun = false
                    } else Log.d("", "Resuming Service")
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Log.d("", "ACTION_STOP_SERVICE")
                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    Log.d("", "ACTION_PAUSE_SERVICE")
                }
                else -> {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //4
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (PermissionUtils.hasLocationPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    //3
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value) for (location in result.locations) {
                addPathPointToTheLastPolyLine(location)
                Log.d("", "NEW LOCATION ${location.latitude}")
            }
        }
    }

    //1
    private fun addEmptyPolyLine() = CoroutineScope(Dispatchers.Default).launch {
        pathPoints.value.apply {
            add(mutableListOf())
            pathPoints.emit(this)
        }
    }

    //2
    private fun addPathPointToTheLastPolyLine(location: Location?) {
        location?.let {
            val position = LatLng(it.latitude, it.longitude)

            CoroutineScope(Dispatchers.Default).launch {
                pathPoints.value.apply {
                    last().add(position)
                    pathPoints.emit(this)
                }
            }
        }
    }

    private fun startForeGroundService() {
        addEmptyPolyLine()
        CoroutineScope(Dispatchers.Default).launch{
            isTracking.emit(true)
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder =
            NotificationCompat.Builder(this, Constants.TRACKING_NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_run)
                .setContentTitle("RunningApp")
                .setContentText("00:00:00")
                .setContentIntent(getMainActivityPendingIntent())

        startForeground(TRACKING_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        }, FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.TRACKING_NOTIFICATION_CHANNEL_ID,
            Constants.TRACKING_NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

}