package com.elnemr.runningtracker.presentation.util

import android.graphics.Color

object Constants {
    const val REQUEST_CODE_LOCATION_PERMISSIONS = 99
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val TRACKING_NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val TRACKING_NOTIFICATION_CHANNEL_NAME = "tracking"
    const val TRACKING_NOTIFICATION_ID = 1

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val TIMER_UPDATE_INTERVAL = 100L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    enum class SORT_BY {
        TIMESTAMP, TIME_IN_MILLIS, DISTANCE_IN_METER, AVG_SPEED, CALORIES_BURNED,
    }

    val SORT_LIST = listOf(
        "Date",
        "Running Time",
        "Distance",
        "Average Speed",
        "Calories Burned"
    )

    const val SHARED_PREFERENCES_NAME = "sharedPreferences"
    const val KEY_FIRST_TIME = "KEY_FIRST_TIME"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"
}