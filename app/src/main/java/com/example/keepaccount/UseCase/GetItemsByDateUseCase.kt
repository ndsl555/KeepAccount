package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.Item
import com.example.keepaccount.Repository.ISpendItemRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetItemsByDateUseCase(val itemRepository: ISpendItemRepository, dispatcher: CoroutineDispatcher) :
    UseCase<GetItemsByDateUseCase.Parameters, List<Item>>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<List<Item>> {
        return itemRepository.getItemsByDate(parameters.year, parameters.month, parameters.day)
    }

    data class Parameters(
        val year: String,
        val month: String,
        val day: String,
    )
}
