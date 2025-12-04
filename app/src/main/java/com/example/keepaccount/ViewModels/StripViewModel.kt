package com.example.keepaccount.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepaccount.Entity.BudGet
import com.example.keepaccount.UseCase.AddBudGetUseCase
import com.example.keepaccount.UseCase.GetBudGetUseCase
import com.example.keepaccount.UseCase.GetItemsUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.Utils.invoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StripViewModel(
    private val getItemsUseCase: GetItemsUseCase,
    private val addBudGetUseCase: AddBudGetUseCase,
    private val getBudGetUseCase: GetBudGetUseCase,
) : ViewModel() {
    /** ---- UI State ---- **/
    private val _boardUI = MutableStateFlow(StripUIState())
    val boardUI: StateFlow<StripUIState> = _boardUI.asStateFlow()

    /** ---- 公開方法 ---- **/
    fun saveBudGet(
        input: Int,
        year: String,
        month: String,
    ) {
        val budGet = BudGet(id = 1, itemBudGet = input)

        viewModelScope.launch {
            when (addBudGetUseCase(AddBudGetUseCase.Parameters(budGet))) {
                is Result.Success -> {
                    //  只更新 items，不再重設 UI Flow，避免 RecyclerView 消失
                    observeItems(year, month)
                }
                is Result.Error -> {
                    // TODO: show error if needed
                }
            }
        }
    }

    /** 取得當月資料（預算 + 花費 + 排行榜） */
    fun observeItems(
        year: String,
        month: String,
    ) {
        viewModelScope.launch {
            /** 取得預算 */
            val budget =
                when (val b = getBudGetUseCase.invoke()) {
                    is Result.Success -> b.data.itemBudGet
                    else -> 0
                }

            /** 取得當月 Items */
            val monthItems =
                when (val r = getItemsUseCase.invoke()) {
                    is Result.Success -> {
                        r.data.filter {
                            it.itemYear == year && it.itemMonth == month
                        }
                    }
                    else -> emptyList()
                }

            /** 計算花費 */
            val cost = monthItems.sumOf { it.itemPrice }

            /** 排行榜 */
            val rankList =
                monthItems
                    .groupBy { it.itemName }
                    .map { (name, list) ->
                        Example2Item(
                            itemname = name,
                            itemprice = list.sumOf { it.itemPrice }.toInt(),
                        )
                    }
                    .sortedByDescending { it.itemprice }

            /** ------- 一次 Update UI ------- **/
            _boardUI.value =
                StripUIState(
                    budget = budget,
                    cost = cost,
                    rankList = rankList,
                )
        }
    }
}

/** ------------------ UI State ------------------ */

data class StripUIState(
    val budget: Int = 0,
    val cost: Double = 0.0,
    val rankList: List<Example2Item> = emptyList(),
)

data class Example2Item(
    val itemname: String,
    val itemprice: Int,
)
