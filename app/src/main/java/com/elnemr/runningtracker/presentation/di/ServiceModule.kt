package com.elnemr.runningtracker.presentation.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.presentation.ui.main.MainActivity
import com.elnemr.runningtracker.presentation.util.Constants
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context) =
        LocationServices.getFusedLocationProviderClient(context)


    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(@ApplicationContext context: Context) =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )

    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(@ApplicationContext context: Context, pendingIntent: PendingIntent) =
        NotificationCompat.Builder(context, Constants.TRACKING_NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("RunningApp")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)


}