package com.example.keepaccount.Repository

import com.example.keepaccount.DataSource.IBudGetDataSource
import com.example.keepaccount.Entity.BudGet
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BudGetRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: IBudGetDataSource,
) : IBudGetRepository {
    override suspend fun insertBudGet(budGet: BudGet) =
        withContext(ioDispatcher) {
            dataSource.insertBudGet(budGet)
        }

    override suspend fun getLatestBudGet(): Result<BudGet> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getLatestBudGet()
        }
}
