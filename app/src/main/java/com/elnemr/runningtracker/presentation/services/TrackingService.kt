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
import com.elnemr.runningtracker.presentation.util.Constants
import com.elnemr.runningtracker.presentation.util.Constants.ACTION_PAUSE_SERVICE
import com.elnemr.runningtracker.presentation.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.elnemr.runningtracker.presentation.util.Constants.ACTION_STOP_SERVICE
import com.elnemr.runningtracker.presentation.util.Constants.TIMER_UPDATE_INTERVAL
import com.elnemr.runningtracker.presentation.util.Constants.TRACKING_NOTIFICATION_ID
import com.elnemr.runningtracker.presentation.util.LocationHelper
import com.elnemr.runningtracker.presentation.util.LocationUtils
import com.elnemr.runningtracker.presentation.util.polyLines
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// LifecycleService() contains LifecycleOwner for observing Livadate
@AndroidEntryPoint
class TrackingService : Service(), LocationHelper.ILocationListener {

    @Inject
    lateinit var locationHelper: LocationHelper
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    private lateinit var curNotificationBuilder: NotificationCompat.Builder

    private var isFirstRun = true
    private lateinit var notificationManager: NotificationManager
    private var timeRunInSecond = MutableStateFlow(0L)

    companion object {
        val timeRunInMillis = MutableStateFlow(0L)
        val isTracking: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val pathPoints: MutableLiveData<polyLines> = MutableLiveData(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder = baseNotificationBuilder
        CoroutineScope(Dispatchers.Default).launch {
            isTracking.collect {
                updateLocationTracking(it)
                updateNotificationTrackingState(it)
            }
        }
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        curNotificationBuilder.clearActions()

        curNotificationBuilder = baseNotificationBuilder.addAction(
            R.drawable.ic_pause_black_24dp,
            notificationActionText,
            pendingIntent
        )

        notificationManager.notify(TRACKING_NOTIFICATION_ID, curNotificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForeGroundService()
                        isFirstRun = false
                    }
                    startTimer()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("", "ACTION_STOP_SERVICE")
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                else -> {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var timeStarted = 0L

    // time difference between now and timeStarted
    private var lapTime = 0L

    // last second we updated the timeRunInSecond
    private var lastSecondTimeStamp = 0L

    // total run time includes pause and resume (all laps) when resume continue count from where we paused
    private var timeRun = 0L

    private fun startTimer() {
        addEmptyPolyLine()
        CoroutineScope(Dispatchers.Default).launch {
            isTracking.emit(true)
        }

        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted

                // post the new lapTime
                timeRunInMillis.emit(timeRun + lapTime)

                if (timeRunInMillis.value >= lastSecondTimeStamp + 1000L) {
                    timeRunInSecond.emit(timeRunInSecond.value + 1)
                    lastSecondTimeStamp += 1000L
                }

                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService() {
        CoroutineScope(Dispatchers.Default).launch { isTracking.emit(false) }
        isTimerEnabled = false
    }

    override fun onBind(p0: Intent?): IBinder? = null

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (LocationUtils.hasLocationPermissions(this)) {
                locationHelper.startTracking(this)
            }
        } else {
            locationHelper.stopTracking()
        }
    }

    private fun addEmptyPolyLine() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPointToTheLastPolyLine(location: Location?) {
        location?.let {
            val pos = LatLng(it.latitude, it.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun startForeGroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(TRACKING_NOTIFICATION_ID, baseNotificationBuilder.build())

        CoroutineScope(Dispatchers.Default).launch {
            timeRunInSecond.collect {

                Log.d("TAG", "startForeGroundService: $it")
                val notification =
                    curNotificationBuilder.setContentText(LocationUtils.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(TRACKING_NOTIFICATION_ID, notification.build())
            }
        }
    }

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