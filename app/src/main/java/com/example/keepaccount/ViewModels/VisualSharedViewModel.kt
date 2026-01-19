package com.example.keepaccount.ViewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepaccount.UseCase.ExportMonthlyConsumptionToExcelUseCase
import com.example.keepaccount.UseCase.GetItemsByMonthUseCase
import com.example.keepaccount.UseCase.GetItemsByMonthUseCase.*
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class VisualSharedViewModel(
    private val getItemsByMonthUseCase: GetItemsByMonthUseCase,
    private val exportMonthlyConsumptionToExcelUseCase: ExportMonthlyConsumptionToExcelUseCase,
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NO)
    val sortType: StateFlow<SortType> = _sortType

    private val _uiEvent = MutableSharedFlow<VisualUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun setSort(sortType: SortType) {
        _sortType.value = sortType
    }

    fun exportMonthlyConsumptionToExcel(
        year: String,
        month: String,
    ) {
        viewModelScope.launch {
            when (
                val res = getItemsByMonthUseCase(Parameters(year, month))
            ) {
                is Success -> {
                    val sortedItems =
                        res.data.sortedBy {
                            LocalDate.of(
                                it.itemYear.toInt(),
                                it.itemMonth.toInt(),
                                it.itemDay.toInt(),
                            )
                        }

                    val uri = exportMonthlyConsumptionToExcelUseCase(sortedItems)

                    if (uri != null) {
                        _uiEvent.emit(VisualUiEvent.OpenExcel(uri))
                    } else {
                        _uiEvent.emit(VisualUiEvent.ShowError("Excel 匯出失敗"))
                    }
                }

                is Result.Error -> {
                    _uiEvent.emit(VisualUiEvent.ShowError("資料讀取失敗"))
                }
            }
        }
    }
}

sealed class VisualUiEvent {
    data class OpenExcel(val uri: Uri) : VisualUiEvent()

    data class ShowError(val message: String) : VisualUiEvent()
}

enum class SortType {
    COST_DESC,
    COST_ASC,
    NO,
}
