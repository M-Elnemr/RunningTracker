package com.elnemr.runningtracker.domain.usecase.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface IUseCase<T, Params> : CoroutineScope {

    fun getStateFlow(): SharedFlow<T>
    suspend fun execute(params: Params?)
}