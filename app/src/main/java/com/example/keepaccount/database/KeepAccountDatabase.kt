package com.example.keepaccount.database

import com.example.keepaccount.Dao.BarCodeDao
import com.example.keepaccount.Dao.BudGetDao
import com.example.keepaccount.Dao.EventDao
import com.example.keepaccount.Dao.ItemDao

interface KeepAccountDatabase {
    fun barCodeDao(): BarCodeDao

    fun itemDao(): ItemDao

    fun budGetDao(): BudGetDao

    fun eventDao(): EventDao
}
