package com.elnemr.runningtracker.domain.usecase

import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.domain.usecase.base.BaseUseCase
import com.elnemr.runningtracker.presentation.util.Constants.SORT_BY
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchRunsUseCase @Inject constructor() : BaseUseCase<Flow<List<Run>>, SORT_BY>() {
    private suspend fun switchSortBy(sortBy: SORT_BY): Flow<List<Run>> =
        when (sortBy) {
            SORT_BY.TIMESTAMP -> iRepository.getAllRunsSortedByDate()
            SORT_BY.DISTANCE_IN_METER -> iRepository.getAllRunsSortedByDistance()
            SORT_BY.TIME_IN_MILLIS -> iRepository.getAllRunsSortedTimeInMillis()
            SORT_BY.AVG_SPEED -> iRepository.getAllRunsSortedByAverageSpeed()
            SORT_BY.CALORIES_BURNED -> iRepository.getAllRunsSortedByCaloriesBurned()
        }

    override suspend fun execute(params: SORT_BY?) {
        stateFlow.emit(switchSortBy(params!!))
    }
}