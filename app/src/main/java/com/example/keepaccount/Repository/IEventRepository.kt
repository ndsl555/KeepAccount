package com.example.keepaccount.Repository

import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Utils.Result

interface IEventRepository {
    suspend fun insertEvent(event: Event)

    suspend fun getEvents(): Result<List<Event>>

    suspend fun getEvent(id: Int): Result<Event>

    suspend fun updateEvent(event: Event)

    suspend fun deleteEvent(event: Event)
}
