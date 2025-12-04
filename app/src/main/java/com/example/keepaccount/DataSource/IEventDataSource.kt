package com.example.keepaccount.DataSource

import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Utils.Result

interface IEventDataSource {
    suspend fun getEvents(): Result<List<Event>>

    suspend fun getEvent(id: Int): Result<Event>

    suspend fun insertEvent(event: Event)

    suspend fun updateEvent(event: Event)

    suspend fun deleteEvent(event: Event)
}
