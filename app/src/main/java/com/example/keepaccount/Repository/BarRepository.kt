package com.example.keepaccount.Repository

import com.example.keepaccount.DataSource.IBarDataSource
import com.example.keepaccount.Entity.BarEntity
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BarRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: IBarDataSource,
) : IBarRepository {
    override suspend fun insertBar(bar: BarEntity) =
        withContext(ioDispatcher) {
            dataSource.insertBar(bar)
        }

    override suspend fun getLatestBar(): Result<BarEntity> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getLatestBar()
        }
}
