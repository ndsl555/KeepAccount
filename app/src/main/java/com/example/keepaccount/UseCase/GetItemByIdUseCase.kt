package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.Item
import com.example.keepaccount.Repository.ISpendItemRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetItemByIdUseCase(val itemRepository: ISpendItemRepository, dispatcher: CoroutineDispatcher) :
    UseCase<GetItemByIdUseCase.Parameters, Item>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Item> {
        return itemRepository.getItem(parameters.id)
    }

    data class Parameters(
        val id: Int,
    )
}
