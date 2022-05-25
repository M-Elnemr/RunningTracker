package com.elnemr.runningtracker.presentation.viewmodel.state

sealed class MainViewModelState {
    data class OnRunInserted(val successful: Boolean): MainViewModelState()
}