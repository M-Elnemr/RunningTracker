package com.elnemr.runningtracker.domain.usecase

import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.domain.usecase.base.BaseUseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class InsertRunUseCase @Inject constructor() : BaseUseCase<Boolean, Run>() {
    override suspend fun execute(params: Run?) {
        iRepository.insertRun(params!!)
    }
}