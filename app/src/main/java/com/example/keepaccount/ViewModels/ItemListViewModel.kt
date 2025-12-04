package com.example.keepaccount.ViewModels

import androidx.lifecycle.*
import com.example.keepaccount.UseCase.DeleteItemByDateAndNameUseCase
import com.example.keepaccount.UseCase.GetItemsByDateUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.groupBy

class ItemListViewModel(
    private val getItemsByDateUseCase: GetItemsByDateUseCase,
    private val deleteItemByDateAndNameUseCase: DeleteItemByDateAndNameUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<ShowItem>>(emptyList())
    val uiState: StateFlow<List<ShowItem>> = _uiState.asStateFlow()

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
    val cost: Double = 0.0,
    val color: String = "",
)
