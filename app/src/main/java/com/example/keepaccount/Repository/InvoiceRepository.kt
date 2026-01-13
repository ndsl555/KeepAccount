package com.example.keepaccount.Repository

import com.example.keepaccount.DataSource.IInvoiceDataSource
import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InvoiceRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: IInvoiceDataSource,
) : IInvoiceRepository {
    override suspend fun insertInvoice(invoice: InvoiceNumber) =
        withContext(ioDispatcher) {
            dataSource.insertInvoice(invoice)
        }

    override suspend fun getInvoice(): Result<InvoiceNumber> =
        withContext(ioDispatcher) {
            return@withContext dataSource.getInvoice()
        }
}
