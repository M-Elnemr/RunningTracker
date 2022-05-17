package com.elnemr.runningtracker.data.repository.datasource

import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.data.db.RunDAO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(private val runDAO: RunDAO) : ILocalDataSource {
    override suspend fun insertRun(run: Run) = runDAO.insertRun(run)

    override suspend fun deleteRun(run: Run) = runDAO.deleteRun(run)

    override suspend fun getAllRunsSortedByDate(): Flow<List<Run>> = runDAO.getAllRunsSortedByDate()

    override suspend fun getAllRunsSortedTimeInMillis(): Flow<List<Run>> =
        runDAO.getAllRunsSortedTimeInMillis()

    override suspend fun getAllRunsSortedByDistance(): Flow<List<Run>> =
        runDAO.getAllRunsSortedByDistance()

    override suspend fun getAllRunsSortedByAverageSpeed(): Flow<List<Run>> =
        runDAO.getAllRunsSortedByAverageSpeed()

    override suspend fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>> =
        runDAO.getAllRunsSortedByCaloriesBurned()

    override suspend fun getTotalTimeInMillis(): Flow<Long> = runDAO.getTotalTimeInMillis()

    override suspend fun getTotalCaloriesBurned(): Flow<Int> = runDAO.getTotalCaloriesBurned()

    override suspend fun getTotalDistanceInMeters(): Flow<Int> = runDAO.getTotalDistanceInMeters()

    override suspend fun getTotalAvgSpeedInKMH(): Flow<Float> = runDAO.getTotalAvgSpeedInKMH()

}