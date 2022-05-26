package com.elnemr.runningtracker.presentation.viewmodel.state

import com.elnemr.runningtracker.data.db.Run

sealed class MainViewModelState {
    data class OnRunInserted(val successful: Boolean): MainViewModelState()
    data class OnRunsFetched(val runs: List<Run>): MainViewModelState()
}