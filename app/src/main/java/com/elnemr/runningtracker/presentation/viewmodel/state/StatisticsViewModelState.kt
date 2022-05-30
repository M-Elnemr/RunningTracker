package com.elnemr.runningtracker.presentation.viewmodel.state

sealed class StatisticsViewModelState{
    data class OnTotalTotalAvgSpeedFetched(val result: Float): StatisticsViewModelState()
    data class OnTotalCaloriesFetched(val result: Int): StatisticsViewModelState()
    data class OnTotalDistanceFetched(val result: Int): StatisticsViewModelState()
    data class OnTotalTimeInMillisFetched(val result: Long): StatisticsViewModelState()
}
