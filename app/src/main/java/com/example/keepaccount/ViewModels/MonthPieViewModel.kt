import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepaccount.UseCase.GetItemsByMonthUseCase
import com.example.keepaccount.Utils.Result
import com.example.keepaccount.ViewModels.SortType
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.sortedByDescending

class MonthPieViewModel(
    private val getItemsByMonthUseCase: GetItemsByMonthUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PieUIState())
    val uiState: StateFlow<PieUIState> = _uiState.asStateFlow()

    fun sortItems(sortType: SortType) {
        val current = _uiState.value
        val sortedItems =
            when (sortType) {
                SortType.COST_DESC -> current.todayItems.sortedByDescending { it.itemcost }
                SortType.COST_ASC -> current.todayItems.sortedBy { it.itemcost }
                else -> current.todayItems
            }

        _uiState.value =
            current.copy(
                todayItems = sortedItems,
            )
    }

    fun loadMonthData(
        thisYear: String,
        thisMonth: String,
    ) {
        viewModelScope.launch {
            when (val result = getItemsByMonthUseCase.invoke(GetItemsByMonthUseCase.Parameters(thisYear, thisMonth))) {
                is Result.Success -> {
                    val grouped = result.data.groupBy { it.itemName }
                    val totalCost = result.data.sumOf { it.itemPrice.toInt() }

                    val mergedList =
                        grouped.map { (name, list) ->
                            ExampleItem(
                                itemname = name,
                                itemcost = list.sumOf { it.itemPrice.toInt() },
                            )
                        }

                    val pieEntries = mergedList.map { PieEntry(it.itemcost.toFloat(), it.itemname) }
                    val colors =
                        mergedList.map { item ->
                            grouped[item.itemname]!!.first().itemColorcode.toColorInt()
                        }

                    _uiState.value =
                        PieUIState(
                            totalCost = totalCost,
                            pieEntries = pieEntries,
                            pieColors = colors,
                            todayItems = mergedList,
                        )
                }

                is Result.Error -> {
                    // 可加錯誤處理
                    _uiState.value = PieUIState()
                }
            }
        }
    }
}
