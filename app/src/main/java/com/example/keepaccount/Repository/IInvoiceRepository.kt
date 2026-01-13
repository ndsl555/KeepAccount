package com.example.keepaccount.Repository

import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Utils.Result

interface IInvoiceRepository {
    suspend fun insertInvoice(invoice: InvoiceNumber)

    suspend fun getInvoice(): Result<InvoiceNumber>
}
