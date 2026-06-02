package com.example.keepaccount.ViewModels

import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepaccount.UseCase.GetItemsByMonthUseCase
import com.example.keepaccount.Utils.Result
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MonthPieViewModel(
    private val getItemsByMonthUseCase: GetItemsByMonthUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PieUIState())
    val uiState: StateFlow<PieUIState> = _uiState.asStateFlow()

    fun sortItems(sortType: SortType) {
        val current = _uiState.value
        val sortedItems =
            when (sortType) {
                SortType.COST_DESC -> current.todayItems.sortedByDescending { it.itemPrice }
                SortType.COST_ASC -> current.todayItems.sortedBy { it.itemPrice }
                else -> current.todayItems
            }

        _uiState.value =
            current.copy(
                todayItems = sortedItems
            )
    }

    fun loadMonthData(
        thisYear: String,
        thisMonth: String
    ) {
        viewModelScope.launch {
            when (val result = getItemsByMonthUseCase.invoke(GetItemsByMonthUseCase.Parameters(thisYear, thisMonth))) {
                is Result.Success -> {
                    val grouped = result.data.groupBy { it.itemName }
                    val totalCost = result.data.sumOf { it.itemPrice }

                    val mergedList =
                        grouped.map { (name, list) ->
                            ExampleItem(
                                itemName = name,
                                itemPrice = list.sumOf { it.itemPrice },
                                itemColor = list.first().itemColorcode
                            )
                        }

                    val pieEntries = mergedList.map { PieEntry(it.itemPrice.toFloat(), it.itemName) }
                    val colors =
                        mergedList.map { item ->
                            item.itemColor.toColorInt()
                        }

                    _uiState.value =
                        PieUIState(
                            totalCost = totalCost,
                            pieEntries = pieEntries,
                            pieColors = colors,
                            todayItems = mergedList
                        )
                }

                is Result.Error -> {
                    _uiState.value = PieUIState()
                }
            }
        }
    }
}
