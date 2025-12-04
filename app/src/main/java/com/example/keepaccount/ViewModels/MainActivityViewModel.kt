package com.example.keepaccount.ViewModels

import androidx.lifecycle.*
import com.example.keepaccount.Entity.TutorialStatus
import com.example.keepaccount.UseCase.AddTutorialUseCase
import com.example.keepaccount.UseCase.LoadTutorialStateUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val addTutorialUseCase: AddTutorialUseCase,
    private val loadTutorialStateUseCase: LoadTutorialStateUseCase,
) : ViewModel() {
    private val _tutorialState = MutableStateFlow<Boolean?>(null) // null 表示尚未讀
    val tutorialStatus: StateFlow<Boolean?> = _tutorialState // Activity 可能意外改變 ViewModel 狀態，造成不可預期的行為，StateFlow比較好

    fun loadTutorialState() {
        viewModelScope.launch {
            when (val loadResult = loadTutorialStateUseCase.invoke()) {
                is Result.Success -> {
                    _tutorialState.value = loadResult.data.isShown
                    // 成功
                }

                is Result.Error -> {
                    // 失敗
                }
            }
        }
    }

    fun saveTutorialState(state: Boolean) {
        viewModelScope.launch {
            val tutorialStatus = TutorialStatus(id = 1, isShown = state)
            when (addTutorialUseCase.invoke(AddTutorialUseCase.Parameters(tutorialStatus))) {
                is Result.Success -> {
                    _tutorialState.value = state
                }

                is Result.Error -> {
                }
            }
        }
    }
}
