package com.example.keepaccount.ViewModels

import androidx.lifecycle.*
import com.example.keepaccount.UseCase.DeleteItemByDateAndNameUseCase
import com.example.keepaccount.UseCase.GetItemsByDateUseCase
import com.example.keepaccount.UseCase.GetUsedDaysInMonthUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.groupBy
import kotlin.map

class ItemListViewModel(
    private val getItemsByDateUseCase: GetItemsByDateUseCase,
    private val getUsedDaysInMonthUseCase: GetUsedDaysInMonthUseCase,
    private val deleteItemByDateAndNameUseCase: DeleteItemByDateAndNameUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<ShowItem>>(emptyList())
    val uiState: StateFlow<List<ShowItem>> = _uiState.asStateFlow()

    private val _markedDays = MutableStateFlow<List<Int>>(emptyList())
    val markedDays = _markedDays.asStateFlow()

    fun sortItemsByCostDesc() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.sortedByDescending { it.cost }
        }
    }

    fun sortItemsByCostAsc() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.sortedBy { it.cost }
        }
    }

    fun getMarkedDays(
        year: String,
        month: String,
    ) {
        viewModelScope.launch {
            when (val result = getUsedDaysInMonthUseCase(GetUsedDaysInMonthUseCase.Parameters(year, month))) {
                is Result.Success -> {
                    val days =
                        result.data
                            .map { it.toInt() }
                            .distinct()

                    _markedDays.value = days
                }

                is Result.Error -> {
                    _markedDays.value = emptyList()
                }
            }
        }
    }

    fun getItemsByDate(
        year: String,
        month: String,
        day: String,
    ) {
        viewModelScope.launch {
            when (
                val result =
                    getItemsByDateUseCase.invoke(
                        GetItemsByDateUseCase.Parameters(year, month, day),
                    )
            ) {
                is Result.Success -> {
                    val grouped = result.data.groupBy { it.itemName }

                    val mergedList =
                        grouped.map { (name, list) ->
                            ShowItem(
                                name = name,
                                cost = list.sumOf { it.itemPrice },
                                color = list.first().itemColorcode,
                            )
                        }

                    _uiState.value = mergedList
                }

                is Result.Error -> {
                    // TODO handle error
                }
            }
        }
    }

    fun deleteItem(
        year: String,
        month: String,
        day: String,
        name: String,
    ) {
        viewModelScope.launch {
            when (
                val result =
                    deleteItemByDateAndNameUseCase.invoke(
                        DeleteItemByDateAndNameUseCase.Parameters(
                            year,
                            month,
                            day,
                            name,
                        ),
                    )
            ) {
                is Result.Success -> {
                    getItemsByDate(year, month, day)
                }

                is Result.Error -> {
                    // TODO: 可加錯誤提示 UI
                }
            }
        }
    }
}

data class ShowItem(
    val name: String = "",
    val cost: Int = 0,
    val color: String = "",
)
