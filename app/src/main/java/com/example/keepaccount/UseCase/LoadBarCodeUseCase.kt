package com.example.keepaccount.UseCase

import com.example.keepaccount.Entity.BarEntity
import com.example.keepaccount.Repository.IBarRepository
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class LoadBarCodeUseCase(val barRepository: IBarRepository, dispatcher: CoroutineDispatcher) :
    UseCase<Unit, BarEntity>(dispatcher) {
    override suspend fun execute(parameters: Unit): Result<BarEntity> {
        return barRepository.getLatestBar()
    }
}
