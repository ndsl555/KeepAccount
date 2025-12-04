package com.example.keepaccount.ViewModels

import androidx.lifecycle.*
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.UseCase.GetEventsUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventListViewModel(
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
}
