package com.example.keepaccount.DataSource

import com.example.keepaccount.Dao.EventDao
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Error
import com.example.keepaccount.Utils.Result.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class EventDataSource(
    private val dao: EventDao,
    private val ioDispatcher: CoroutineDispatcher,
) : IEventDataSource {
    override suspend fun getEvents(): Result<List<Event>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val item = dao.getEvents()
                Success(item)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getEvent(id: Int): Result<Event> =
        withContext(ioDispatcher) {
            return@withContext try {
                val item = dao.getEvent(id)
                Success(item)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun insertEvent(event: Event) =
        withContext(ioDispatcher) {
            dao.insert(event)
        }

    override suspend fun updateEvent(event: Event) =
        withContext(ioDispatcher) {
            dao.update(event)
        }

    override suspend fun deleteEvent(event: Event) =
        withContext(ioDispatcher) {
            dao.delete(event)
        }
}
