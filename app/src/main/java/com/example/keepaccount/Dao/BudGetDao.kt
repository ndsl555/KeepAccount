package com.example.keepaccount.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.keepaccount.Entity.BudGet

@Dao
interface BudGetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudGet(budGet: BudGet)

    @Query("SELECT * FROM BudGetTable LIMIT 1")
    suspend fun getLatestBudGet(): BudGet
}
