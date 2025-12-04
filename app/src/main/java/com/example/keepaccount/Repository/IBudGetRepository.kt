package com.example.keepaccount.Repository

import com.example.keepaccount.Entity.BudGet
import com.example.keepaccount.Utils.Result

interface IBudGetRepository {
    suspend fun insertBudGet(budGet: BudGet)

    suspend fun getLatestBudGet(): Result<BudGet>
}
