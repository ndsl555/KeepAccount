package com.example.keepaccount.Repository

import com.example.keepaccount.DataSource.ITutorialDataSource
import com.example.keepaccount.Entity.TutorialStatus
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TutorialStateRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: ITutorialDataSource,
) : ITutorialStateRepository {
    override suspend fun insertSate(tutorialStatus: TutorialStatus) =
        withContext(ioDispatcher) {
            dataSource.insertSate(tutorialStatus)
        }

    override suspend fun getLatestSate(): Result<TutorialStatus> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getLatestSate()
        }
}
