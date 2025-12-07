package com.example.keepaccount.UseCase

import com.example.keepaccount.Repository.ISpendItemRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetUsedDaysInMonthUseCase(val itemRepository: ISpendItemRepository, dispatcher: CoroutineDispatcher) :
    UseCase<GetUsedDaysInMonthUseCase.Parameters, List<String>>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<List<String>> {
        return itemRepository.getUsedDaysInMonth(parameters.year, parameters.month)
    }

    data class Parameters(
        val year: String,
        val month: String,
    )
}
