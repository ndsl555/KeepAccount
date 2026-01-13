package com.example.keepaccount.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.keepaccount.Entity.InvoiceNumber

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoiceNumber: InvoiceNumber)

    @Query("SELECT * FROM invoicetable LIMIT 1")
    suspend fun getInvoice(): InvoiceNumber
}
