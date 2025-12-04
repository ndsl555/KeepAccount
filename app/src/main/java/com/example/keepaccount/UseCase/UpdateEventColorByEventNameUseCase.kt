package com.example.keepaccount.UseCase

import com.example.keepaccount.Repository.ISpendItemRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class UpdateEventColorByEventNameUseCase(val spendItemRepository: ISpendItemRepository, dispatcher: CoroutineDispatcher) :
    UseCase<UpdateEventColorByEventNameUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        spendItemRepository.updateColorCodeByName(parameters.name, parameters.colorCode)
        return Success(Unit)
    }

    data class Parameters(
        val name: String,
        val colorCode: String,
    )
}
