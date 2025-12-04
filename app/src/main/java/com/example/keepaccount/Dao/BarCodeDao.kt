package com.example.keepaccount.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.keepaccount.Entity.BarEntity

@Dao
interface BarCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBar(bar: BarEntity)

    @Query("SELECT * FROM BarTable LIMIT 1")
    suspend fun getLatestBar(): BarEntity
}
