package com.example.keepaccount.Repository

import com.example.keepaccount.DataSource.IEventDataSource
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class EventRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: IEventDataSource,
) : IEventRepository {
    override suspend fun insertEvent(event: Event) =
        withContext(ioDispatcher) {
            dataSource.insertEvent(event)
        }

    override suspend fun getEvents(): Result<List<Event>> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getEvents()
        }

    override suspend fun getEvent(id: Int): Result<Event> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getEvent(id)
        }

    override suspend fun updateEvent(event: Event) =
        withContext(ioDispatcher) {
            dataSource.updateEvent(event)
        }

    override suspend fun deleteEvent(event: Event) =
        withContext(ioDispatcher) {
            dataSource.deleteEvent(event)
        }
}
