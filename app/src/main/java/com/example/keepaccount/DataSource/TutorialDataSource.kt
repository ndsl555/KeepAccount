package com.example.keepaccount.DataSource

import com.example.keepaccount.Dao.TutorialShownStateDao
import com.example.keepaccount.Entity.TutorialStatus
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TutorialDataSource(
    private val dao: TutorialShownStateDao,
    private val ioDispatcher: CoroutineDispatcher,
) : ITutorialDataSource {
    override suspend fun insertSate(tutorialStatus: TutorialStatus) =
        withContext(ioDispatcher) {
            dao.insertSate(tutorialStatus)
        }

    override suspend fun getLatestSate(): Result<TutorialStatus> =
        withContext(ioDispatcher) {
            return@withContext try {
                val status = dao.getLatestSate()
                Result.Success(status)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
