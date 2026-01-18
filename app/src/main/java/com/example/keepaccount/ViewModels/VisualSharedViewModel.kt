package com.example.keepaccount.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepaccount.UseCase.ExportMonthlyConsumptionToExcelUseCase
import com.example.keepaccount.UseCase.GetItemsByMonthUseCase
import com.example.keepaccount.UseCase.GetItemsByMonthUseCase.*
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.Result.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class VisualSharedViewModel(
    private val getItemsByMonthUseCase: GetItemsByMonthUseCase,
    private val exportMonthlyConsumptionToExcelUseCase: ExportMonthlyConsumptionToExcelUseCase,
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NO)
    val sortType: StateFlow<SortType> = _sortType

    fun setSort(sortType: SortType) {
        _sortType.value = sortType
    }

    fun exportMonthlyConsumptionToExcel(
        year: String,
        month: String,
    ) {
        viewModelScope.launch {
            when (
                val res = getItemsByMonthUseCase.invoke(Parameters(year, month))
            ) {
                is Success -> {
                    val items = res.data
                    val sortedItems =
                        items.sortedBy { item ->
                            LocalDate.of(item.itemYear.toInt(), item.itemMonth.toInt(), item.itemDay.toInt())
                        }

                    sortedItems.forEach {
                        println(it.itemYear + it.itemMonth + it.itemDay)
                    }
                    exportMonthlyConsumptionToExcelUseCase(sortedItems)
                }
                is Result.Error -> {
                    println(res.exception)
                }
            }
        }
    }
}

enum class SortType {
    COST_DESC,
    COST_ASC,
    NO,
}
