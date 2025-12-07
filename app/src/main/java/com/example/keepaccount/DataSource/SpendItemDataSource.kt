package com.example.keepaccount.DataSource

import com.example.keepaccount.Dao.ItemDao
import com.example.keepaccount.Entity.Item
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Error
import com.example.keepaccount.Utils.Result.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SpendItemDataSource(
    private val dao: ItemDao,
    private val ioDispatcher: CoroutineDispatcher,
) : ISpendItemDataSource {
    override suspend fun getItems(): Result<List<Item>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val item = dao.getItems()
                Success(item)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getItem(id: Int): Result<Item> =
        withContext(ioDispatcher) {
            return@withContext try {
                val item = dao.getItem(id)
                Success(item)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun insertItem(item: Item) =
        withContext(ioDispatcher) {
            dao.insert(item)
        }

    override suspend fun updateItem(item: Item) =
        withContext(ioDispatcher) {
            dao.update(item)
        }

    // 根據 name 更新 colorcode
    override suspend fun updateColorCodeByName(
        name: String,
        newColorCode: String,
    ) = withContext(ioDispatcher) {
        dao.updateColorCodeByName(name, newColorCode)
    }

    // 根據 name 刪除所有符合的資料
    override suspend fun deleteByName(name: String) =
        withContext(ioDispatcher) {
            dao.deleteByName(name)
        }

    override suspend fun deleteItem(item: Item) =
        withContext(ioDispatcher) {
            dao.delete(item)
        }

    override suspend fun getItemsByDate(
        year: String,
        month: String,
        day: String,
    ): Result<List<Item>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val item = dao.getItemsByDate(year, month, day)
                Success(item)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getItemsByMonth(
        year: String,
        month: String,
    ): Result<List<Item>> {
        return withContext(ioDispatcher) {
            return@withContext try {
                val item = dao.getItemsByMonth(year, month)
                Success(item)
            } catch (e: Exception) {
                Error(e)
            }
        }
    }

    override suspend fun deleteByDateAndName(
        year: String,
        month: String,
        day: String,
        name: String,
    ) = withContext(ioDispatcher) {
        dao.deleteByDateAndName(year, month, day, name)
    }

    override suspend fun getUsedDaysInMonth(
        year: String,
        month: String,
    ): Result<List<String>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val days = dao.getUsedDaysInMonth(year, month)
                Success(days)
            } catch (e: Exception) {
                Error(e)
            }
        }
}
