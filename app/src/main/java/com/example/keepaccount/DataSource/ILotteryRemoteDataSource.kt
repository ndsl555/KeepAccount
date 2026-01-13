package com.example.keepaccount.DataSource

import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Utils.Result

interface ILotteryRemoteDataSource {
    suspend fun getLotteryNumber(): Result<InvoiceNumber>
}
