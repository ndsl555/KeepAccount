package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.Item
import com.example.keepaccount.Repository.ISpendItemRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class DeleteItemUseCase(val itemRepository: ISpendItemRepository, dispatcher: CoroutineDispatcher) :
    UseCase<DeleteItemUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        itemRepository.deleteItem(parameters.item)
        return Success(Unit)
    }

    data class Parameters(
        val item: Item,
    )
}
