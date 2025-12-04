package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Repository.IEventRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class UpdateEventUseCase(val eventRepository: IEventRepository, dispatcher: CoroutineDispatcher) :
    UseCase<UpdateEventUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        val res = eventRepository.updateEvent(parameters.event)
        return Success(res)
    }

    data class Parameters(
        val event: Event,
    )
}
