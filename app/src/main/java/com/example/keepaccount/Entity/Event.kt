
package com.example.keepaccount.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val eventName: String,
    @ColumnInfo(name = "colorcode")
    val eventColorCode: String,
)
