package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Repository.IInvoiceRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class LoadInvoiceUseCase(val invoiceRepository: IInvoiceRepository, dispatcher: CoroutineDispatcher) :
    UseCase<Unit, InvoiceNumber>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<InvoiceNumber> {
        return invoiceRepository.getInvoice()
    }
}
