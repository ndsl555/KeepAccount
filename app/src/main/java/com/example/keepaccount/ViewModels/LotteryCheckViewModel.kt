package com.example.keepaccount.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Entity.isReady
import com.example.keepaccount.QrWinningResult
import com.example.keepaccount.QrWinningType
import com.example.keepaccount.UseCase.LoadInvoiceUseCase
import com.example.keepaccount.UseCase.LotteryCheckUseCase
import com.example.keepaccount.UseCase.SaveInvoiceUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.any
import kotlin.collections.zip

class LotteryCheckViewModel(
    private val useCase: LotteryCheckUseCase,
    private val saveInvoiceUseCase: SaveInvoiceUseCase,
    private val loadInvoiceUseCase: LoadInvoiceUseCase,
) : ViewModel() {
    private val _lotteryNumber = MutableStateFlow(InvoiceNumber(1, "", "", "", emptyList()))
    val lotteryNumber: StateFlow<InvoiceNumber> = _lotteryNumber.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        getLotteryNumberFromDB()
    }

    fun checkWinningByQr(invoiceNumber: String): QrWinningResult {
        val lottery = _lotteryNumber.value

        if (!lottery.isReady()) {
            return QrWinningResult(QrWinningType.NONE)
        }

        if (invoiceNumber == lottery.specialistPrize) {
            return QrWinningResult(QrWinningType.SPECIALIST_PRIZE, "🎉恭喜！中「特別獎」 1000萬")
        }

        if (invoiceNumber == lottery.specialPrize) {
            return QrWinningResult(QrWinningType.SPECIAL_PRIZE, "🎉恭喜！中「特獎」 200萬")
        }

        lottery.firstPrize.forEach { prize ->
            when {
                invoiceNumber == prize ->
                    return QrWinningResult(QrWinningType.FIRST_PRIZE, "🎉恭喜！中「頭獎」 20萬")

                invoiceNumber.takeLast(7) == prize.takeLast(7) ->
                    return QrWinningResult(QrWinningType.FIRST_PRIZE, "🎉恭喜！中 4萬")

                invoiceNumber.takeLast(6) == prize.takeLast(6) ->
                    return QrWinningResult(QrWinningType.FIRST_PRIZE, "🎉恭喜！中 1萬")

                invoiceNumber.takeLast(5) == prize.takeLast(5) ->
                    return QrWinningResult(QrWinningType.FIRST_PRIZE, "🎉恭喜！中 4千")

                invoiceNumber.takeLast(4) == prize.takeLast(4) ->
                    return QrWinningResult(QrWinningType.FIRST_PRIZE, "🎉恭喜！中 1千")

                invoiceNumber.takeLast(3) == prize.takeLast(3) ->
                    return QrWinningResult(QrWinningType.FIRST_PRIZE, "🎉恭喜！中 200")
            }
        }

        return QrWinningResult(QrWinningType.NONE, "沒中 , 再接再厲 \uD83D\uDE05")
    }

    fun getLotteryNumberFromDB() {
        viewModelScope.launch {
            Log.e("LotteryCheckViewModel", "Start getLotteryNumberFromDB")
            _isLoading.value = true

            try {
                val localResult = loadInvoiceUseCase.invoke()

                if (localResult is Result.Success) {
                    val localData = localResult.data
                    Log.e("LotteryCheckViewModel", "DB Success: $localData")
                    // 先更新 UI
                    _lotteryNumber.value = localData

                    // 再抓網路
                    fetchAndUpdateIfDifferent(localData)
                } else if (localResult is Result.Error) {
                    Log.e("LotteryCheckViewModel", "DB Error: ${localResult.exception}")
                    // DB 失敗 → 直接抓網路
                    fetchAndUpdateIfDifferent(null)
                }
            } catch (e: Exception) {
                Log.e("LotteryCheckViewModel", "Exception in getLotteryNumberFromDB", e)
                fetchAndUpdateIfDifferent(null)
            }
        }
    }

    private suspend fun fetchAndUpdateIfDifferent(localData: InvoiceNumber?) {
        try {
            _isLoading.value = true
            val networkResult = useCase.invoke()
            if (networkResult is Result.Success) {
                val remoteData = networkResult.data
                Log.e("LotteryCheckViewModel", "Network Success: $remoteData")

                if (localData == null || !localData.isReady() || isDifferent(localData, remoteData)) {
                    Log.e(
                        "LotteryCheckViewModel",
                        "Updating DB & UI because data is new or different",
                    )
                    _lotteryNumber.value = remoteData
                    saveInvoiceUseCase.invoke(SaveInvoiceUseCase.Parameters(remoteData))
                } else {
                    Log.e("LotteryCheckViewModel", "DB is already up-to-date")
                }
                _isLoading.value = false
            } else if (networkResult is Result.Error) {
                Log.e(
                    "LotteryCheckViewModel",
                    "Network Error: ${networkResult.exception}",
                )
                _isLoading.value = false
            }
        } catch (e: Exception) {
            Log.e("LotteryCheckViewModel", "Exception in fetchAndUpdateIfDifferent", e)
        }
    }

    // helper: 比對 DB 與網路資料
    private fun isDifferent(
        local: InvoiceNumber,
        remote: InvoiceNumber,
    ): Boolean {
        if (local.topic != remote.topic) return true
        if (local.firstPrize.size != remote.firstPrize.size) return true
        if (local.firstPrize.zip(remote.firstPrize).any { it.first != it.second }) return true
        if (local.specialistPrize != remote.specialistPrize) return true
        if (local.specialPrize != remote.specialPrize) return true
        return false
    }
}
