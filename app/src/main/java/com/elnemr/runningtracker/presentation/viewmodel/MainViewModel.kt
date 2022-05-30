package com.elnemr.runningtracker.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.domain.usecase.FetchRunsUseCase
import com.elnemr.runningtracker.domain.usecase.InsertRunUseCase
import com.elnemr.runningtracker.presentation.base.viewmodel.BaseViewModel
import com.elnemr.runningtracker.presentation.util.Constants
import com.elnemr.runningtracker.presentation.viewmodel.state.MainViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val insertRunUseCase: InsertRunUseCase,
    private val fetchRunsUseCase: FetchRunsUseCase
) : BaseViewModel<MainViewModelState>() {

    init {
        viewModelScope.launch {
            launch { insertRunUseCase.getStateFlow().buffer().collect { onRunInserted(it) } }
            launch { fetchRunsUseCase.getStateFlow().buffer().collect { onRunsFetched(it) } }
        }
    }

    fun fetchRuns(sortedBy: Constants.SORT_BY) {
        fetchRunsUseCase.invoke(sortedBy)
    }
    fun insertRun(run: Run) {
        insertRunUseCase.invoke(run)
    }

    private fun onRunInserted(success: Boolean) {
        viewModelScope.launch {
            mediator.emit(MainViewModelState.OnRunInserted(success))
        }
    }

    private fun onRunsFetched(it: Flow<List<Run>>) {
        viewModelScope.launch {
            it.buffer().collect {
                mediator.emit(MainViewModelState.OnRunsFetched(it))
            }
        }
    }

}