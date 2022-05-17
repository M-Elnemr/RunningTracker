package com.elnemr.runningtracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedTimeInMillis(): Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAverageSpeed(): Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): Flow<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): Flow<Int>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistanceInMeters(): Flow<Int>

    @Query("SELECT SUM(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeedInKMH(): Flow<Float>
}