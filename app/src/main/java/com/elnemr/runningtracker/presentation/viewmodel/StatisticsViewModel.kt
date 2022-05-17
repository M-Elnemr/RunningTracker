package com.elnemr.runningtracker.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.elnemr.runningtracker.domain.usecase.InsertRunUseCase
import com.elnemr.runningtracker.presentation.base.viewmodel.BaseViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.StatisticsViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val insertRunUseCase: InsertRunUseCase) :
    BaseViewModel<StatisticsViewModelState>() {

    init {
        viewModelScope.launch {
            launch { insertRunUseCase.getStateFlow().buffer().collect { onRunInserted(it) } }
        }
    }

    private fun onRunInserted(success: Boolean) {

    }

}