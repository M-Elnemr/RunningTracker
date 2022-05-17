package com.elnemr.runningtracker.domain.repository

import com.elnemr.runningtracker.data.db.Run
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun insertRun(run: Run)

    suspend fun deleteRun(run: Run)

    suspend fun getAllRunsSortedByDate(): Flow<List<Run>>

    suspend fun getAllRunsSortedTimeInMillis(): Flow<List<Run>>

    suspend fun getAllRunsSortedByDistance(): Flow<List<Run>>

    suspend fun getAllRunsSortedByAverageSpeed(): Flow<List<Run>>

    suspend fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>>

    suspend fun getTotalTimeInMillis(): Flow<Long>

    suspend fun getTotalCaloriesBurned(): Flow<Int>

    suspend fun getTotalDistanceInMeters(): Flow<Int>

    suspend fun getTotalAvgSpeedInKMH(): Flow<Float>
}