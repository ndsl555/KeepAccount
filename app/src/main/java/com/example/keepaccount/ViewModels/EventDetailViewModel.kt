package com.example.keepaccount.ViewModels

import androidx.lifecycle.*
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Transaction.DatabaseTransactionRunner
import com.example.keepaccount.UseCase.DeleteEventUseCase
import com.example.keepaccount.UseCase.DeleteItemByNameUseCase
import com.example.keepaccount.UseCase.GetEventByIdNameUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getEventByIdNameUseCase: GetEventByIdNameUseCase,
    private val deleteItemByNameUseCase: DeleteItemByNameUseCase,
    private val transactionRunner: DatabaseTransactionRunner, // ← 只新增這一行
) : ViewModel() {
    private val _uiState = MutableStateFlow(Event(0, "", ""))
    val uiState: StateFlow<Event> = _uiState.asStateFlow()

    fun deleteItem(event: Event) {
        viewModelScope.launch {
            //  開一個 Transaction，把兩個刪除包在一起
            transactionRunner {
                when (val r1 = deleteEventUseCase(DeleteEventUseCase.Parameters(event))) {
                    is Result.Success -> {
                        println("刪除 Event 成功")
                    }
                    is Result.Error -> {
                        println("刪除 Event 失敗")
                        return@transactionRunner
                    }
                }

                when (val r2 = deleteItemByNameUseCase(DeleteItemByNameUseCase.Parameters(event.eventName))) {
                    is Result.Success -> {
                        println("刪除 Item 成功")
                    }
                    is Result.Error -> {
                        println("刪除 Item 失敗")
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
