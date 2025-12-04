package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.TutorialStatus
import com.example.keepaccount.Repository.ITutorialStateRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class LoadTutorialStateUseCase(val tutorialStateRepository: ITutorialStateRepository, dispatcher: CoroutineDispatcher) :
    UseCase<Unit, TutorialStatus>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<TutorialStatus> {
        return tutorialStateRepository.getLatestSate()
    }
}
