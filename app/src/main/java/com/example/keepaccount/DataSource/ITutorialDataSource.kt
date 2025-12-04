package com.example.keepaccount.DataSource

import com.example.keepaccount.Entity.TutorialStatus
import com.example.keepaccount.Utils.Result

interface ITutorialDataSource {
    suspend fun insertSate(tutorialStatus: TutorialStatus)

    suspend fun getLatestSate(): Result<TutorialStatus>
}
