package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.BarEntity
import com.example.keepaccount.Repository.IBarRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class AddBarCodeUseCase(val barRepository: IBarRepository, dispatcher: CoroutineDispatcher) :
    UseCase<AddBarCodeUseCase.Parameters, Unit>(dispatcher) {
    override suspend fun execute(parameters: Parameters): Result<Unit> {
        barRepository.insertBar(parameters.barEntity)
        return Success(Unit)
    }

    data class Parameters(
        val barEntity: BarEntity,
    )
}
