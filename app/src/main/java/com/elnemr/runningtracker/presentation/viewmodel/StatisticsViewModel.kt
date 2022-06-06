package com.elnemr.runningtracker.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.domain.usecase.*
import com.elnemr.runningtracker.presentation.base.viewmodel.BaseViewModel
import com.elnemr.runningtracker.presentation.util.Constants
import com.elnemr.runningtracker.presentation.viewmodel.state.StatisticsViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getTotalTimeInMillisUseCase: GetTotalTimeInMillisUseCase,
    private val getTotalDistanceUseCase: GetTotalDistanceUseCase,
    private val getTotalCaloriesUseCase: GetTotalCaloriesUseCase,
    private val getTotalAvgSpeedUseCase: GetTotalAvgSpeedUseCase,
    private val fetchRunsUseCase: FetchRunsUseCase
) : BaseViewModel<StatisticsViewModelState>() {

    init {
        viewModelScope.launch {
            launch {
                getTotalTimeInMillisUseCase.getStateFlow().buffer()
                    .collect { onTotalTimeInMillisFetched(it) }
            }
            launch {
                getTotalDistanceUseCase.getStateFlow().buffer()
                    .collect { onTotalDistanceFetched(it) }
            }
            launch {
                getTotalCaloriesUseCase.getStateFlow().buffer()
                    .collect { onTotalCaloriesFetched(it) }
            }
            launch {
                getTotalAvgSpeedUseCase.getStateFlow().buffer()
                    .collect { onTotalTotalAvgSpeedFetched(it) }
            }
            launch {
                fetchRunsUseCase.getStateFlow().buffer()
                    .collect { onRunsFetched(it) }
            }
        }
    }

    private fun onRunsFetched(result: Flow<List<Run>>) {
        viewModelScope.launch {
            result.buffer().collect {
                mediator.emit(StatisticsViewModelState.OnRunsFetched(it))
            }
        }
    }

    fun getAllStatistics() {
        getTotalAvgSpeedUseCase.invoke(null)
        getTotalDistanceUseCase.invoke(null)
        getTotalCaloriesUseCase.invoke(null)
        getTotalTimeInMillisUseCase.invoke(null)
    }

    fun fetchRunByDate(sortedBy: Constants.SORT_BY) {
        fetchRunsUseCase.invoke(sortedBy)
    }

    private fun onTotalTotalAvgSpeedFetched(result: Flow<Float>) {
        viewModelScope.launch {
            result.buffer().collect {
                mediator.emit(StatisticsViewModelState.OnTotalTotalAvgSpeedFetched(it))
            }
        }
    }

    private fun onTotalCaloriesFetched(result: Flow<Int>) {
        viewModelScope.launch {
            result.buffer().collect {
                mediator.emit(StatisticsViewModelState.OnTotalCaloriesFetched(it))
            }
        }
    }

    private fun onTotalDistanceFetched(result: Flow<Int>) {
        viewModelScope.launch {
            result.buffer().collect {
                mediator.emit(StatisticsViewModelState.OnTotalDistanceFetched(it))
            }

        }
    }

    private fun onTotalTimeInMillisFetched(result: Flow<Long>) {
        viewModelScope.launch {
            result.buffer().collect {
                mediator.emit(StatisticsViewModelState.OnTotalTimeInMillisFetched(it))
            }
        }
    }

}