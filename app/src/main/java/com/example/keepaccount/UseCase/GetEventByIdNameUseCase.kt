package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Repository.IEventRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetEventByIdNameUseCase(val eventRepository: IEventRepository, dispatcher: CoroutineDispatcher) :
    UseCase<GetEventByIdNameUseCase.Parameters, Event>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Event> {
        return eventRepository.getEvent(parameters.id)
    }

    data class Parameters(
        val id: Int,
    )
}
