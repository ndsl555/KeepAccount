package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Repository.IInvoiceRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class SaveInvoiceUseCase(val invoiceRepository: IInvoiceRepository, dispatcher: CoroutineDispatcher) :
    UseCase<SaveInvoiceUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        invoiceRepository.insertInvoice(
            parameters.invoiceNumber,
        )
        return Success(Unit)
    }

    data class Parameters(
        val invoiceNumber: InvoiceNumber,
    )
}
