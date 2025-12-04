package com.example.keepaccount.ViewModels

import androidx.lifecycle.*
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Entity.Item
import com.example.keepaccount.UseCase.AddItemUseCase
import com.example.keepaccount.UseCase.GetEventsUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddItemViewModel(
    private val addItemUseCase: AddItemUseCase,
    private val getEventsUseCase: GetEventsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<Event>>(emptyList())
    val uiState: StateFlow<List<Event>> = _uiState.asStateFlow()

    fun getAllEvents() {
        viewModelScope.launch {
            when (val result = getEventsUseCase.invoke()) {
                is Result.Success -> {
                    _uiState.value = result.data // data 是 List<Item>
                }
                is Result.Error -> { }
            }
        }
    }

    fun isEntryValid(
        itemName: String,
        itemPrice: String,
        itemColorcode: String,
        itemYear: String,
        itemMonth: String,
        itemDay: String,
    ): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemColorcode.isBlank() || itemYear.isBlank() ||
            itemMonth.isBlank() || itemDay.isBlank()
        ) {
            return false
        }
        return true
    }

    fun addNewItem(item: Item) {
        viewModelScope.launch {
            when (val result = addItemUseCase.invoke(AddItemUseCase.Parameters(item))) {
                is Result.Success -> {
                }

                is Result.Error -> {
                }
            }
        }
    }
}
