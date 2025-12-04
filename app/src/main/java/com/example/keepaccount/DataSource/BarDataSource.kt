package com.example.keepaccount.DataSource

import com.example.keepaccount.Dao.BarCodeDao
import com.example.keepaccount.Entity.BarEntity
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BarDataSource(
    private val dao: BarCodeDao,
    private val ioDispatcher: CoroutineDispatcher,
) : IBarDataSource {
    override suspend fun insertBar(bar: BarEntity) =
        withContext(ioDispatcher) {
            dao.insertBar(bar)
        }

    override suspend fun getLatestBar(): Result<BarEntity> =
        withContext(ioDispatcher) {
            return@withContext try {
                val bar = dao.getLatestBar()
                Result.Success(bar)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
