package com.elnemr.runningtracker.data.di

import android.content.Context
import androidx.room.Room
import com.elnemr.runningtracker.data.db.RunningDatabase
import com.elnemr.runningtracker.data.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(@ApplicationContext context: Context): RunningDatabase =
        Room.databaseBuilder(context, RunningDatabase::class.java, Constants.DATABASE).build()

    @Provides
    @Singleton
    fun provideRunDao(runningDatabase: RunningDatabase) =
        runningDatabase.runDao()
}