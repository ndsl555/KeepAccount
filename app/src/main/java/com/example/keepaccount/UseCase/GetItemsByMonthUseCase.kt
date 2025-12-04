package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.Item
import com.example.keepaccount.Repository.ISpendItemRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetItemsByMonthUseCase(val itemRepository: ISpendItemRepository, dispatcher: CoroutineDispatcher) :
    UseCase<GetItemsByMonthUseCase.Parameters, List<Item>>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<List<Item>> {
        return itemRepository.getItemsByMonth(parameters.year, parameters.month)
    }

    data class Parameters(
        val year: String,
        val month: String,
    )
}
