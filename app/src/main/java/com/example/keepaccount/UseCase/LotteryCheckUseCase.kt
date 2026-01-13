package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Repository.ILotteryRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class LotteryCheckUseCase(private val lotteryRepository: ILotteryRepository, dispatcher: CoroutineDispatcher) :
    UseCase<Unit, InvoiceNumber>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<InvoiceNumber> {
        return lotteryRepository.getLotteryNumber()
    }
}
