package com.example.keepaccount.Repository

import com.example.keepaccount.DataSource.ISpendItemDataSource
import com.example.keepaccount.Entity.Item
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SpendItemRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: ISpendItemDataSource,
) : ISpendItemRepository {
    override suspend fun insertItem(item: Item) =
        withContext(ioDispatcher) {
            dataSource.insertItem(item)
        }

    override suspend fun getItems(): Result<List<Item>> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getItems()
        }

    override suspend fun getItem(id: Int): Result<Item> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getItem(id)
        }

    override suspend fun updateItem(item: Item) =
        withContext(ioDispatcher) {
            dataSource.updateItem(item)
        }

    // 根據 name 更新 colorcode
    override suspend fun updateColorCodeByName(
        name: String,
        newColorCode: String,
    ) = withContext(ioDispatcher) {
        dataSource.updateColorCodeByName(name, newColorCode)
    }

    // 根據 name 刪除所有符合的資料
    override suspend fun deleteByName(name: String) =
        withContext(ioDispatcher) {
            dataSource.deleteByName(name)
        }

    override suspend fun deleteItem(item: Item) =
        withContext(ioDispatcher) {
            dataSource.deleteItem(item)
        }

    override suspend fun getItemsByDate(
        year: String,
        month: String,
        day: String,
    ): Result<List<Item>> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getItemsByDate(year, month, day)
        }

    override suspend fun getItemsByMonth(
        year: String,
        month: String,
    ): Result<List<Item>> {
        return withContext(ioDispatcher) {
            return@withContext dataSource.getItemsByMonth(year, month)
        }
    }

    override suspend fun deleteByDateAndName(
        year: String,
        month: String,
        day: String,
        name: String,
    ) = withContext(ioDispatcher) {
        dataSource.deleteByDateAndName(year, month, day, name)
    }

    override suspend fun getUsedDaysInMonth(
        year: String,
        month: String,
    ): Result<List<String>> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getUsedDaysInMonth(year, month)
        }
}
