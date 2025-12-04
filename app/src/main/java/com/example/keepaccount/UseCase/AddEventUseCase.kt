package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Repository.IEventRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class AddEventUseCase(val eventRepository: IEventRepository, dispatcher: CoroutineDispatcher) :
    UseCase<AddEventUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        eventRepository.insertEvent(parameters.event)
        return Success(Unit)
    }

    data class Parameters(
        val event: Event,
    )
}
