package com.example.keepaccount.Repository

import com.example.keepaccount.Entity.Item
import com.example.keepaccount.Utils.Result

interface ISpendItemRepository {
    suspend fun insertItem(item: Item)

    suspend fun getItems(): Result<List<Item>>

    suspend fun getItem(id: Int): Result<Item>

    suspend fun updateItem(item: Item)

    // 根據 name 更新 colorcode
    suspend fun updateColorCodeByName(
        name: String,
        newColorCode: String,
    )

    // 根據 name 刪除資料
    suspend fun deleteByName(name: String)

    suspend fun deleteItem(item: Item)

    suspend fun getItemsByDate(
        year: String,
        month: String,
        day: String,
    ): Result<List<Item>>

    suspend fun getItemsByMonth(
        year: String,
        month: String,
    ): Result<List<Item>>

    suspend fun deleteByDateAndName(
        year: String,
        month: String,
        day: String,
        name: String,
    )

    suspend fun getUsedDaysInMonth(
        year: String,
        month: String,
    ): Result<List<String>>
}
