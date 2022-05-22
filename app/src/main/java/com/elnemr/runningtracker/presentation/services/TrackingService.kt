package com.elnemr.runningtracker.presentation.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.presentation.ui.main.MainActivity
import com.elnemr.runningtracker.presentation.util.Constants
import com.elnemr.runningtracker.presentation.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.elnemr.runningtracker.presentation.util.Constants.TRACKING_NOTIFICATION_ID
import com.elnemr.runningtracker.presentation.util.LocationHelper
import com.elnemr.runningtracker.presentation.util.LocationUtils
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


typealias polyLine = MutableList<LatLng>
typealias polyLines = MutableList<polyLine>

// LifecycleService() contains LifecycleOwner for observing Livadate
class TrackingService : Service(), LocationHelper.ILocationListener {

    private var isFirstRun = true
    private val locationHelper = LocationHelper()

    companion object {
        val isTracking: MutableStateFlow<Boolean> = MutableStateFlow(false)
//        val pathPoints: MutableStateFlow<polyLines> = MutableStateFlow(mutableListOf())
        val pathPoints: MutableLiveData<polyLines> = MutableLiveData(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
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
                    } else startForeGroundService()
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Log.d("", "ACTION_STOP_SERVICE")
                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                else -> {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() = CoroutineScope(Dispatchers.Default).launch{ isTracking.emit(false) }

    override fun onBind(p0: Intent?): IBinder? = null

    //4
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (LocationUtils.hasLocationPermissions(this)) {
                locationHelper.startTracking(this, this)
            }
        } else {
            locationHelper.stopTracking()
        }
    }

    private fun addEmptyPolyLine() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

//
//    private fun addEmptyPolyLine() = CoroutineScope(Dispatchers.Default).launch {
//        pathPoints.value.apply {
//            add(mutableListOf())
//            pathPoints.emit(this)
//        }
//    }


    private fun addPathPointToTheLastPolyLine(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

//    private fun addPathPointToTheLastPolyLine(location: Location?) {
//        location?.let {
//            val position = LatLng(it.latitude, it.longitude)
//
//            CoroutineScope(Dispatchers.Default).launch {
//                pathPoints.value.apply {
//                    last().add(position)
//                    pathPoints.emit(this)
//                }
//            }
//        }
//    }

    private fun startForeGroundService() {
        addEmptyPolyLine()
        CoroutineScope(Dispatchers.Default).launch {
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

    override fun onLocationResult(location: Location) {
        if (isTracking.value) {
            addPathPointToTheLastPolyLine(location)
            Log.d("", "NEW LOCATION ${location.latitude}")
        }
    }

}