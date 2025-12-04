package com.example.keepaccount.Repository

import com.example.keepaccount.Entity.BarEntity
import com.example.keepaccount.Utils.Result

interface IBarRepository {
    suspend fun insertBar(bar: BarEntity)

    suspend fun getLatestBar(): Result<BarEntity>
}
