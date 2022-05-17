package com.elnemr.runningtracker.presentation.base.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

abstract class BaseViewModel<T>: ViewModel() {

    protected val mediator: MutableSharedFlow<T> = MutableSharedFlow(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        mediator.distinctUntilChanged()
    }

    fun getStateFlow() = mediator

}