package com.elnemr.runningtracker.presentation.viewmodel.state

import com.elnemr.runningtracker.data.db.Run

sealed class StatisticsViewModelState{
    data class OnTotalTotalAvgSpeedFetched(val result: Float): StatisticsViewModelState()
    data class OnTotalCaloriesFetched(val result: Int): StatisticsViewModelState()
    data class OnTotalDistanceFetched(val result: Int): StatisticsViewModelState()
    data class OnTotalTimeInMillisFetched(val result: Long): StatisticsViewModelState()
    data class OnRunsFetched(val runs: List<Run>): StatisticsViewModelState()
}
