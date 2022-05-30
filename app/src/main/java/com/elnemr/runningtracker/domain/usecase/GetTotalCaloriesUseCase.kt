package com.elnemr.runningtracker.domain.usecase

import com.elnemr.runningtracker.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalCaloriesUseCase @Inject constructor() : BaseUseCase<Flow<Int>, Boolean>() {
    override suspend fun execute(params: Boolean?) {
        stateFlow.emit(iRepository.getTotalCaloriesBurned())
    }
}