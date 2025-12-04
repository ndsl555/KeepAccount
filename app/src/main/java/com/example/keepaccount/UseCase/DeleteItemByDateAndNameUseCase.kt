package com.example.keepaccount.UseCase

import com.example.keepaccount.Repository.ISpendItemRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class DeleteItemByDateAndNameUseCase(val itemRepository: ISpendItemRepository, dispatcher: CoroutineDispatcher) :
    UseCase<DeleteItemByDateAndNameUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        itemRepository.deleteByDateAndName(parameters.year, parameters.month, parameters.day, parameters.name)
        return Success(Unit)
    }

    data class Parameters(
        val year: String,
        val month: String,
        val day: String,
        val name: String,
    )
}
