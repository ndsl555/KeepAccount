package com.example.keepaccount.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.keepaccount.Entity.TutorialStatus

@Dao
interface TutorialShownStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSate(tutorialStatus: TutorialStatus)

    @Query("SELECT * FROM TUTORIALSTATUSTABLE LIMIT 1")
    suspend fun getLatestSate(): TutorialStatus
}
