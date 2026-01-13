package com.example.keepaccount.Repository

import com.example.keepaccount.DataSource.ILotteryRemoteDataSource
import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Utils.Result

class LotteryRepository(private val lotteryDataRemoteSource: ILotteryRemoteDataSource) : ILotteryRepository {
    override suspend fun getLotteryNumber(): Result<InvoiceNumber> {
        return lotteryDataRemoteSource.getLotteryNumber()
    }
}
