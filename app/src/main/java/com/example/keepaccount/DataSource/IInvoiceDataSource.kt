package com.example.keepaccount.DataSource

import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Utils.Result

interface IInvoiceDataSource {
    suspend fun insertInvoice(invoice: InvoiceNumber)

    suspend fun getInvoice(): Result<InvoiceNumber>
}
