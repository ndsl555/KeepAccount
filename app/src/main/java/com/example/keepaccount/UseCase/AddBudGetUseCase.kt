package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.BudGet
import com.example.keepaccount.Repository.IBudGetRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class AddBudGetUseCase(val budGetRepository: IBudGetRepository, dispatcher: CoroutineDispatcher) :
    UseCase<AddBudGetUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        budGetRepository.insertBudGet(parameters.budGet)
        return Success(Unit)
    }

    data class Parameters(
        val budGet: BudGet,
    )
}
