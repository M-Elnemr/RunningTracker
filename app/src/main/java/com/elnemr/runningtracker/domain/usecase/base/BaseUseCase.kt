package com.elnemr.runningtracker.domain.usecase.base

import com.elnemr.runningtracker.domain.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

abstract class BaseUseCase<T, params>(
    protected val stateFlow: MutableSharedFlow<T> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
) : IUseCase<T, params> {

    @Inject
    protected lateinit var iRepository: IRepository
    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    init {
        stateFlow.distinctUntilChanged()
    }

    override fun getStateFlow(): SharedFlow<T> = stateFlow

    operator fun invoke(params: params?) {
        launch(Dispatchers.IO) {
            execute(params)
        }
    }
}