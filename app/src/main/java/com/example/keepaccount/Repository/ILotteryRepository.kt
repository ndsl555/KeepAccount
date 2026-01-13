package com.example.keepaccount.Repository

import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Utils.Result

interface ILotteryRepository {
    suspend fun getLotteryNumber(): Result<InvoiceNumber>
}
