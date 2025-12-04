package com.example.keepaccount.DataSource

import com.example.keepaccount.Entity.BarEntity
import com.example.keepaccount.Utils.Result

interface IBarDataSource {
    suspend fun insertBar(bar: BarEntity)

    suspend fun getLatestBar(): Result<BarEntity>
}
