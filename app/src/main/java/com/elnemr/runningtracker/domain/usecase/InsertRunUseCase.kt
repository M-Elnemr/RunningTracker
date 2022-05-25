package com.elnemr.runningtracker.domain.usecase

import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class InsertRunUseCase @Inject constructor() : BaseUseCase<Boolean, Run>() {
    override suspend fun execute(params: Run?) {
        try {
            iRepository.insertRun(params!!)
            stateFlow.emit(true)
        }catch (e: Exception){
            stateFlow.emit(false)
        }
    }
}