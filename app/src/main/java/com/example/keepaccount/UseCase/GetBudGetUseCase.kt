package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.BudGet
import com.example.keepaccount.Repository.IBudGetRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetBudGetUseCase(val budGetRepository: IBudGetRepository, dispatcher: CoroutineDispatcher) :
    UseCase<Unit, BudGet>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<BudGet> {
        return budGetRepository.getLatestBudGet()
    }
}
