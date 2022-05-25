package com.elnemr.runningtracker.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.domain.usecase.InsertRunUseCase
import com.elnemr.runningtracker.presentation.base.viewmodel.BaseViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.MainViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val insertRunUseCase: InsertRunUseCase) :
    BaseViewModel<MainViewModelState>() {

        init {
            viewModelScope.launch {
                launch { insertRunUseCase.getStateFlow().buffer().collect{ onRunInserted(it) } }
            }
        }

    fun insertRun(run: Run){
        insertRunUseCase.invoke(run)
    }

    private fun onRunInserted(success: Boolean) {
        viewModelScope.launch {
            mediator.emit(MainViewModelState.OnRunInserted(success))
        }
    }


}