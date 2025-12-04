package com.example.keepaccount.DataSource

import com.example.keepaccount.Entity.BudGet
import com.example.keepaccount.Utils.Result

interface IBudGetDataSource {
    suspend fun insertBudGet(budGet: BudGet)

    suspend fun getLatestBudGet(): Result<BudGet>
}
