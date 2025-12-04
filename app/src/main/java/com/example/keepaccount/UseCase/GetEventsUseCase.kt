package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Repository.IEventRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetEventsUseCase(val itemEventRepository: IEventRepository, dispatcher: CoroutineDispatcher) :
    UseCase<Unit, List<Event>>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<List<Event>> {
        return itemEventRepository.getEvents()
    }
}
