package com.example.keepaccount.Repository

import com.example.keepaccount.Entity.TutorialStatus
import com.example.keepaccount.Utils.Result

interface ITutorialStateRepository {
    suspend fun insertSate(tutorialStatus: TutorialStatus)

    suspend fun getLatestSate(): Result<TutorialStatus>
}
