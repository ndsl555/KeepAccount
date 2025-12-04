package com.example.keepaccount.DataSource

import com.example.keepaccount.Dao.BudGetDao
import com.example.keepaccount.Entity.BudGet
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BudGetDataSource(
    private val dao: BudGetDao,
    private val ioDispatcher: CoroutineDispatcher,
) : IBudGetDataSource {
    override suspend fun insertBudGet(budGet: BudGet) =
        withContext(ioDispatcher) {
            dao.insertBudGet(budGet)
        }

    override suspend fun getLatestBudGet(): Result<BudGet> =
        withContext(ioDispatcher) {
            return@withContext try {
                val budGet = dao.getLatestBudGet()
                Result.Success(budGet)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
