package com.example.keepaccount.ViewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VisualSharedViewModel : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NO)
    val sortType: StateFlow<SortType> = _sortType

    fun setSort(sortType: SortType) {
        _sortType.value = sortType
    }
}

enum class SortType {
    COST_DESC,
    COST_ASC,
    NO,
}
