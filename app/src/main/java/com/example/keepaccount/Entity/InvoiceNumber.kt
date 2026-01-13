package com.example.keepaccount.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.collections.all
import kotlin.collections.isNotEmpty
import kotlin.text.isNotBlank

@Entity(tableName = "InvoiceTable")
data class InvoiceNumber(
    @PrimaryKey
    var id: Int = 1,
    val specialistPrize: String, // 特獎
    val specialPrize: String, // 特別獎
    val firstPrize: List<String>, // 頭獎（3 組）
)

/**
 * 判斷發票號碼是否已完整取得
 */
fun InvoiceNumber.isReady(): Boolean {
    return specialistPrize.isNotBlank() &&
        specialPrize.isNotBlank() &&
        firstPrize.isNotEmpty() &&
        firstPrize.all { it.isNotBlank() }
}
