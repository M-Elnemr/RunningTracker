package com.elnemr.runningtracker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.elnemr.runningtracker.domain.usecase.GetTotalAvgSpeedUseCase
import com.elnemr.runningtracker.domain.usecase.GetTotalCaloriesUseCase
import com.elnemr.runningtracker.domain.usecase.GetTotalDistanceUseCase
import com.elnemr.runningtracker.domain.usecase.GetTotalTimeInMillisUseCase
import com.elnemr.runningtracker.presentation.base.viewmodel.BaseViewModel
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
    private val getTotalAvgSpeedUseCase: GetTotalAvgSpeedUseCase
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
        }
    }

    fun getAllStatistics(){
        getTotalAvgSpeedUseCase.invoke(null)
        getTotalDistanceUseCase.invoke(null)
        getTotalCaloriesUseCase.invoke(null)
        getTotalTimeInMillisUseCase.invoke(null)
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