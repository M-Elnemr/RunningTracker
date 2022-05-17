package com.elnemr.runningtracker.data.repository

import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.data.repository.datasource.ILocalDataSource
import com.elnemr.runningtracker.domain.repository.IRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val iLocalDataSource: ILocalDataSource) :
    IRepository {

    override suspend fun insertRun(run: Run) = iLocalDataSource.insertRun(run)

    override suspend fun deleteRun(run: Run) = iLocalDataSource.deleteRun(run)

    override suspend fun getAllRunsSortedByDate(): Flow<List<Run>> =
        iLocalDataSource.getAllRunsSortedByDate()

    override suspend fun getAllRunsSortedTimeInMillis(): Flow<List<Run>> =
        iLocalDataSource.getAllRunsSortedTimeInMillis()

    override suspend fun getAllRunsSortedByDistance(): Flow<List<Run>> =
        iLocalDataSource.getAllRunsSortedByDistance()

    override suspend fun getAllRunsSortedByAverageSpeed(): Flow<List<Run>> =
        iLocalDataSource.getAllRunsSortedByAverageSpeed()

    override suspend fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>> =
        iLocalDataSource.getAllRunsSortedByCaloriesBurned()

    override suspend fun getTotalTimeInMillis(): Flow<Long> = iLocalDataSource.getTotalTimeInMillis()

    override suspend fun getTotalCaloriesBurned(): Flow<Int> = iLocalDataSource.getTotalCaloriesBurned()

    override suspend fun getTotalDistanceInMeters(): Flow<Int> = iLocalDataSource.getTotalDistanceInMeters()

    override suspend fun getTotalAvgSpeedInKMH(): Flow<Float> = iLocalDataSource.getTotalAvgSpeedInKMH()


}