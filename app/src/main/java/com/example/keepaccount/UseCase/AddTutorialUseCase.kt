package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.TutorialStatus
import com.example.keepaccount.Repository.ITutorialStateRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class AddTutorialUseCase(val tutorialStateRepository: ITutorialStateRepository, dispatcher: CoroutineDispatcher) :
    UseCase<AddTutorialUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        tutorialStateRepository.insertSate(parameters.tutorialStatus)
        return Success(Unit)
    }

    data class Parameters(
        val tutorialStatus: TutorialStatus,
    )
}
