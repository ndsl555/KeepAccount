package com.example.keepaccount.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "ItemTable")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val itemName: String,
    @ColumnInfo(name = "price")
    val itemPrice: Int,
    @ColumnInfo(name = "colorcode")
    val itemColorcode: String,
    @ColumnInfo(name = "year")
    val itemYear: String,
    @ColumnInfo(name = "month")
    val itemMonth: String,
    @ColumnInfo(name = "day")
    val itemDay: String,
)

/**
 * Returns the passed in price in currency format.
 */
fun Item.getFormattedPrice(): String = NumberFormat.getCurrencyInstance().format(itemPrice)
