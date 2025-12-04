package com.example.keepaccount.ViewModels

import androidx.lifecycle.*
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Transaction.DatabaseTransactionRunner
import com.example.keepaccount.UseCase.AddEventUseCase
import com.example.keepaccount.UseCase.GetEventByIdNameUseCase
import com.example.keepaccount.UseCase.UpdateEventColorByEventNameUseCase
import com.example.keepaccount.UseCase.UpdateEventUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEventViewModel(
    private val addEventUseCase: AddEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val getEventByIdNameUseCase: GetEventByIdNameUseCase,
    private val updateEventColorByEventNameUseCase: UpdateEventColorByEventNameUseCase,
    private val transactionRunner: DatabaseTransactionRunner, // ← 只新增這一行
) : ViewModel() {
    private val _uiState = MutableStateFlow(Event(0, "", ""))
    val uiState: StateFlow<Event> = _uiState.asStateFlow()

    fun isEntryValid(
        itemName: String,
        itemColorcode: String,
    ): Boolean {
        return !(itemName.isBlank() || itemColorcode.isBlank())
    }

    fun addNewEvent(event: Event) {
        viewModelScope.launch {
            when (val result = addEventUseCase.invoke(AddEventUseCase.Parameters(event))) {
                is Result.Success -> {
                }

                is Result.Error -> {
                }
            }
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            //  將兩個 UseCase 包在同一個 transaction 中執行
            transactionRunner {
                when (val r1 = updateEventUseCase(UpdateEventUseCase.Parameters(event))) {
                    is Result.Success -> {
                        println("更新 Event 成功")
                    }

                    is Result.Error -> {
                        println("更新 Event 失敗")
                        return@transactionRunner // 中止 transaction
                    }
                }

                when (
                    val r2 =
                        updateEventColorByEventNameUseCase(
                            UpdateEventColorByEventNameUseCase.Parameters(
                                event.eventName,
                                event.eventColorCode,
                            ),
                        )
                ) {
                    is Result.Success -> {
                        println("更新同品項顏色成功")
                    }

                    is Result.Error -> {
                        println("更新同品項顏色失敗")
                    }
                }
            }
        }
    }

    fun retrieveItem(id: Int) {
        viewModelScope.launch {
            when (val result = getEventByIdNameUseCase.invoke(GetEventByIdNameUseCase.Parameters(id))) {
                is Result.Success -> {
                    _uiState.value = result.data
                }

                is Result.Error -> {
                }
            }
        }
    }
}
