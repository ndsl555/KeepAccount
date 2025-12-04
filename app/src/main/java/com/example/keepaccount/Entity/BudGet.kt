package com.example.keepaccount.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BudGetTable")
class BudGet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    @ColumnInfo(name = "bud_permonth")
    val itemBudGet: Int = 0,
)
