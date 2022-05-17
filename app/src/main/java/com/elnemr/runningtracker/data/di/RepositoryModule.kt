package com.elnemr.runningtracker.data.di

import android.content.Context
import androidx.room.Room
import com.elnemr.runningtracker.data.db.RunningDatabase
import com.elnemr.runningtracker.data.repository.RepositoryImpl
import com.elnemr.runningtracker.data.repository.datasource.ILocalDataSource
import com.elnemr.runningtracker.data.repository.datasource.LocalDataSourceImpl
import com.elnemr.runningtracker.data.util.Constants
import com.elnemr.runningtracker.domain.repository.IRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(repositoryImpl: RepositoryImpl): IRepository = repositoryImpl

    @Provides
    @Singleton
    fun provideLocalDataSource(localDataSourceImpl: LocalDataSourceImpl): ILocalDataSource =
        localDataSourceImpl
}