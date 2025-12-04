package com.example.keepaccount.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tutorialStatusTable")
data class TutorialStatus(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    @ColumnInfo(name = "isShown")
    val isShown: Boolean = false,
)
